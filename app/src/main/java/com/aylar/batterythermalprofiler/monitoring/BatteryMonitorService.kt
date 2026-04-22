package com.aylar.batterythermalprofiler.monitoring

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.ForegroundServiceStartNotAllowedException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.BatteryManager
import android.os.IBinder
import android.os.PowerManager
import android.content.pm.ApplicationInfo
import androidx.core.app.NotificationCompat
import com.aylar.batterythermalprofiler.R
import com.aylar.batterythermalprofiler.core.data.battery.BatteryIntentParser
import com.aylar.batterythermalprofiler.core.domain.repository.BatteryRepository
import com.aylar.batterythermalprofiler.core.domain.repository.ThermalRepository
import com.aylar.batterythermalprofiler.core.domain.repository.WakelockRepository
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject
import com.aylar.batterythermalprofiler.core.domain.model.ThermalSnapshot
import com.aylar.batterythermalprofiler.core.domain.model.WakelockEvent
import java.io.File

@AndroidEntryPoint
class BatteryMonitorService : Service() {
    @Inject lateinit var batteryRepository: BatteryRepository
    @Inject lateinit var thermalRepository: ThermalRepository
    @Inject lateinit var wakelockRepository: WakelockRepository

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val parsed = BatteryIntentParser.parseActionBatteryChanged(
                intent = intent,
                nowMillis = System.currentTimeMillis(),
            ) ?: return

            // Persist snapshot and update notification (fire-and-forget).
            MonitoringScope.launch {
                batteryRepository.insert(parsed.snapshot)
            }

            updateNotification(parsed.snapshot.level, parsed.snapshot.temperatureDeciC)
        }
    }

    private val powerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Phase 5: keep the hooks in place; persistence added later if needed.
            // ACTION_DEVICE_IDLE_MODE_CHANGED / ACTION_POWER_SAVE_MODE_CHANGED
        }
    }

    private var thermalListener: PowerManager.OnThermalStatusChangedListener? = null
    private var thermalExecutor: ExecutorService? = null

    override fun onCreate() {
        super.onCreate()
        ensureNotificationChannel()
        try {
            startForeground(NOTIFICATION_ID, buildNotification(level = null, tempDeciC = null))
        } catch (e: ForegroundServiceStartNotAllowedException) {
            // Android may block FGS start from background/boot. Don't crash the process.
            stopSelf()
            return
        } catch (t: Throwable) {
            // Be defensive: if we can't become a foreground service, exit cleanly.
            stopSelf()
            return
        }
        registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        registerReceiver(powerReceiver, IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED))
        registerReceiver(powerReceiver, IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED))

        startThermalMonitoring()
        startWakelockMonitoringIfDebug()
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        unregisterReceiver(powerReceiver)
        stopThermalMonitoring()
        MonitoringScope.cancelAll()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Keep running; receiver gets sticky broadcast immediately after register.
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startThermalMonitoring() {
        val pm = getSystemService(PowerManager::class.java)

        if (Build.VERSION.SDK_INT >= 29) {
            val executor = Executors.newSingleThreadExecutor().also { thermalExecutor = it }
            val listener = PowerManager.OnThermalStatusChangedListener { status ->
                MonitoringScope.launch {
                    thermalRepository.insert(
                        ThermalSnapshot(
                            timestampMillis = System.currentTimeMillis(),
                            thermalStatus = status,
                            cpuTempDeciC = null,
                        ),
                    )
                }
            }
            thermalListener = listener
            pm.addThermalStatusListener(executor, listener)
        } else {
            // API 26–28 fallback: poll a common sysfs CPU thermal zone.
            MonitoringScope.launch {
                while (true) {
                    val deciC = readCpuTempDeciC()
                    if (deciC != null) {
                        thermalRepository.insert(
                            ThermalSnapshot(
                                timestampMillis = System.currentTimeMillis(),
                                thermalStatus = -1,
                                cpuTempDeciC = deciC,
                            ),
                        )
                    }
                    delay(30_000)
                }
            }
        }
    }

    private fun stopThermalMonitoring() {
        val pm = getSystemService(PowerManager::class.java)
        if (Build.VERSION.SDK_INT >= 29) {
            thermalListener?.let { pm.removeThermalStatusListener(it) }
        }
        thermalListener = null
        thermalExecutor?.shutdownNow()
        thermalExecutor = null
    }

    private fun readCpuTempDeciC(): Int? {
        val file = File("/sys/class/thermal/thermal_zone0/temp")
        if (!file.exists()) return null
        return runCatching {
            val raw = file.readText().trim()
            val v = raw.toLong()
            // Most devices expose millidegree C, e.g. 42000. Convert to deci-C (420).
            when {
                v > 10_000 -> (v / 100).toInt()
                else -> v.toInt()
            }
        }.getOrNull()
    }

    private fun startWakelockMonitoringIfDebug() {
        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (!isDebuggable) return

        MonitoringScope.launch {
            while (true) {
                val output = runCatching {
                    val p = Runtime.getRuntime().exec(arrayOf("sh", "-c", "dumpsys power"))
                    p.inputStream.bufferedReader().readText()
                }.getOrNull()

                if (!output.isNullOrBlank()) {
                    val events = WakelockDumpsysParser.parse(output, nowMillis = System.currentTimeMillis())
                    events.forEach { e ->
                        wakelockRepository.insert(e)
                    }
                }
                delay(30_000)
            }
        }
    }

    private fun ensureNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Battery Monitor",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Persistent battery monitoring"
        }
        manager.createNotificationChannel(channel)
    }

    private fun updateNotification(level: Int, tempDeciC: Int) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(level, tempDeciC))
    }

    private fun buildNotification(level: Int?, tempDeciC: Int?): Notification {
        val title = "Battery monitor"
        val content = when {
            level == null || tempDeciC == null -> "Starting…"
            else -> {
                val tempC = tempDeciC / 10f
                "${level}% • ${tempC}°C"
            }
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "battery_monitor"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val i = Intent(context, BatteryMonitorService::class.java)
            context.startForegroundService(i)
        }
    }
}

