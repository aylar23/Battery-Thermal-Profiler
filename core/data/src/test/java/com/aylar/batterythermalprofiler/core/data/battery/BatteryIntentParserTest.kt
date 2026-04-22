package com.aylar.batterythermalprofiler.core.data.battery

import android.content.Intent
import android.os.BatteryManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BatteryIntentParserTest {
    @Test
    fun parseActionBatteryChanged_nullWhenWrongAction() {
        val intent = Intent("nope")
        val parsed = BatteryIntentParser.parseActionBatteryChanged(intent, nowMillis = 123L)
        assertNull(parsed)
    }

    @Test
    fun parseActionBatteryChanged_parsesAndNormalizesPercent() {
        val intent = Intent(Intent.ACTION_BATTERY_CHANGED).apply {
            putExtra(BatteryManager.EXTRA_LEVEL, 50)
            putExtra(BatteryManager.EXTRA_SCALE, 100)
            putExtra(BatteryManager.EXTRA_TEMPERATURE, 320)
            putExtra(BatteryManager.EXTRA_VOLTAGE, 3850)
            putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_CHARGING)
            putExtra(BatteryManager.EXTRA_PLUGGED, BatteryManager.BATTERY_PLUGGED_USB)
            putExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)
        }

        val parsed = BatteryIntentParser.parseActionBatteryChanged(intent, nowMillis = 999L)
        assertNotNull(parsed)
        assertEquals(999L, parsed!!.snapshot.timestampMillis)
        assertEquals(50, parsed.snapshot.level)
        assertEquals(320, parsed.snapshot.temperatureDeciC)
        assertEquals(3850, parsed.snapshot.voltageMv)
        assertEquals(BatteryManager.BATTERY_STATUS_CHARGING, parsed.snapshot.status)
        assertEquals(BatteryManager.BATTERY_PLUGGED_USB, parsed.snapshot.plugged)
        assertEquals(BatteryManager.BATTERY_HEALTH_GOOD, parsed.health)
    }
}

