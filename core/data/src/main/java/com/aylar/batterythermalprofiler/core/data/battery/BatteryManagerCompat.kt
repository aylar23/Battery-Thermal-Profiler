package com.aylar.batterythermalprofiler.core.data.battery

import android.os.BatteryManager

object BatteryManagerCompat {
    fun currentNowMicroA(batteryManager: BatteryManager): Long? =
        batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            .takeIf { it != Long.MIN_VALUE && it != 0L }

    fun chargeCounterMicroAh(batteryManager: BatteryManager): Long? =
        batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            .takeIf { it != Long.MIN_VALUE && it != 0L }
}

