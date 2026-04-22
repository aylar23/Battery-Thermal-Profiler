package com.aylar.batterythermalprofiler.monitoring

import com.aylar.batterythermalprofiler.core.domain.model.WakelockEvent

/**
 * Best-effort parser for `dumpsys power` output on debug builds.
 *
 * This is intentionally conservative: if a line doesn't match known patterns,
 * it is ignored.
 */
object WakelockDumpsysParser {
    private val tagRegex = Regex("""tag="?([^",\s]+)"?""", RegexOption.IGNORE_CASE)
    private val nameRegex = Regex("""name="?([^",\s]+)"?""", RegexOption.IGNORE_CASE)

    fun parse(dumpsysPowerOutput: String, nowMillis: Long): List<WakelockEvent> {
        val lines = dumpsysPowerOutput.lineSequence()
        val events = mutableListOf<WakelockEvent>()

        for (line in lines) {
            val trimmed = line.trim()
            if (!trimmed.contains("WakeLock", ignoreCase = true)) continue

            val tag = tagRegex.find(trimmed)?.groupValues?.getOrNull(1)
                ?: nameRegex.find(trimmed)?.groupValues?.getOrNull(1)
                ?: continue

            events += WakelockEvent(
                packageName = "unknown",
                tag = tag,
                acquiredAtMillis = nowMillis,
                durationMs = 0L,
            )
        }

        return events
    }
}

