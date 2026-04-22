package com.aylar.batterythermalprofiler.monitoring

import org.junit.Assert.assertEquals
import org.junit.Test

class WakelockDumpsysParserTest {
    @Test
    fun parse_extractsTagOrName() {
        val out = """
            Some header
            WakeLock{ tag="LocationManagerService" uid=1000 }
            WakeLock{ name="GCM_CONN_ALARM" uid=10012 }
            not a match
        """.trimIndent()

        val events = WakelockDumpsysParser.parse(out, nowMillis = 123L)
        assertEquals(listOf("LocationManagerService", "GCM_CONN_ALARM"), events.map { it.tag })
    }
}

