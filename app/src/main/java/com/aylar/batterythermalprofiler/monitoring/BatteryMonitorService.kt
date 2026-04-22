package com.aylar.batterythermalprofiler.monitoring

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aylar.batterythermalprofiler.R
import com.aylar.batterythermalprofiler.core.data.battery.BatteryIntentParser
import com.aylar.batterythermalprofiler.core.domain.repository.BatteryRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BatteryMonitorService : Service() {
    @Inject lateinit var batteryRepository: BatteryRepository

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

    override fun onCreate() {
        super.onCreate()
        ensureNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(level = null, tempDeciC = null))
        registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Keep running; receiver gets sticky broadcast immediately after register.
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

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

