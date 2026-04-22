package com.aylar.batterythermalprofiler.core.data.battery

import android.content.Intent
import android.os.BatteryManager
import com.aylar.batterythermalprofiler.core.domain.model.BatterySnapshot

object BatteryIntentParser {
    data class ParsedBatteryChanged(
        val snapshot: BatterySnapshot,
        val health: Int,
    )

    fun parseActionBatteryChanged(
        intent: Intent,
        nowMillis: Long,
    ): ParsedBatteryChanged? {
        if (intent.action != Intent.ACTION_BATTERY_CHANGED) return null

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val temperatureDeciC = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        val voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, Int.MIN_VALUE)

        if (level < 0 || scale <= 0) return null
        if (temperatureDeciC == Int.MIN_VALUE || voltageMv == Int.MIN_VALUE) return null

        val percent = ((level * 100f) / scale).toInt().coerceIn(0, 100)

        return ParsedBatteryChanged(
            snapshot = BatterySnapshot(
                timestampMillis = nowMillis,
                level = percent,
                temperatureDeciC = temperatureDeciC,
                voltageMv = voltageMv,
                status = status,
                plugged = plugged,
            ),
            health = health,
        )
    }
}

