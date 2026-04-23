package com.aylar.batterythermalprofiler.feature.report

import com.aylar.batterythermalprofiler.core.domain.model.AppPowerEntry
import com.aylar.batterythermalprofiler.core.domain.model.BatterySnapshot
import com.aylar.batterythermalprofiler.core.domain.model.ThermalSnapshot
import com.aylar.batterythermalprofiler.core.domain.model.WakelockEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object ReportHtmlBuilder {
    fun buildHtml(
        title: String,
        windowStartMillis: Long,
        windowEndMillis: Long,
        avgDrainPerHour: Double?,
        peakTempC: Double?,
        worstApp: AppPowerEntry?,
        apps: List<AppPowerEntry>,
        thermal: List<ThermalSnapshot>,
        wakelocks: List<WakelockEvent>,
        recommendations: List<String>,
    ): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val start = fmt.format(Date(windowStartMillis))
        val end = fmt.format(Date(windowEndMillis))

        val appsRows = apps.joinToString(separator = "\n") { e ->
            "<tr><td>${escape(e.packageName)}</td><td>${formatMs(e.backgroundMs)}</td><td>${"%.2f".format(e.estimatedMah)}</td><td>${e.wakelocksHeld}</td></tr>"
        }

        val recs = if (recommendations.isEmpty()) {
            "<li>No recommendations yet.</li>"
        } else {
            recommendations.joinToString(separator = "\n") { "<li>${escape(it)}</li>" }
        }

        val thermalRows = thermal.sortedBy { it.timestampMillis }.takeLast(40).joinToString(separator = "\n") { t ->
            val temp = t.cpuTempDeciC?.let { "%.1f".format(it / 10.0) } ?: "N/A"
            "<tr><td>${fmt.format(Date(t.timestampMillis))}</td><td>${t.thermalStatus}</td><td>$temp</td></tr>"
        }

        val wakelockRows = wakelocks.sortedByDescending { it.durationMs }.take(40).joinToString(separator = "\n") { w ->
            "<tr><td>${escape(w.packageName)}</td><td>${escape(w.tag)}</td><td>${fmt.format(Date(w.acquiredAtMillis))}</td><td>${formatMs(w.durationMs)}</td></tr>"
        }

        return """
            <!doctype html>
            <html>
              <head>
                <meta charset="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>${escape(title)}</title>
                <style>
                  body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif; padding: 24px; color: #111; }
                  h1 { margin: 0 0 8px; }
                  .muted { color: #555; }
                  .card { border: 1px solid #e6e6e6; border-radius: 12px; padding: 16px; margin: 16px 0; }
                  table { width: 100%; border-collapse: collapse; }
                  th, td { text-align: left; padding: 8px; border-bottom: 1px solid #eee; vertical-align: top; }
                  th { background: #fafafa; }
                  .kpi { display: flex; gap: 16px; flex-wrap: wrap; }
                  .kpi > div { min-width: 180px; }
                  code { background: #f6f6f6; padding: 2px 6px; border-radius: 6px; }
                </style>
              </head>
              <body>
                <h1>${escape(title)}</h1>
                <div class="muted">Window: <code>$start</code> → <code>$end</code></div>

                <div class="card">
                  <h2>Summary</h2>
                  <div class="kpi">
                    <div><b>Avg drain</b><br/>${avgDrainPerHour?.let { "%.2f %%/h".format(it) } ?: "N/A"}</div>
                    <div><b>Peak temp</b><br/>${peakTempC?.let { "%.1f °C".format(it) } ?: "N/A"}</div>
                    <div><b>Worst app</b><br/>${worstApp?.packageName ?: "N/A"}</div>
                  </div>
                </div>

                <div class="card">
                  <h2>Per-app (top)</h2>
                  <table>
                    <thead><tr><th>Package</th><th>Background</th><th>Estimated mAh</th><th>Wakelocks</th></tr></thead>
                    <tbody>
                      $appsRows
                    </tbody>
                  </table>
                </div>

                <div class="card">
                  <h2>Thermal events (latest)</h2>
                  <table>
                    <thead><tr><th>Time</th><th>Status</th><th>CPU temp</th></tr></thead>
                    <tbody>
                      $thermalRows
                    </tbody>
                  </table>
                </div>

                <div class="card">
                  <h2>Wakelocks (top)</h2>
                  <table>
                    <thead><tr><th>Package</th><th>Tag</th><th>Acquired</th><th>Duration</th></tr></thead>
                    <tbody>
                      $wakelockRows
                    </tbody>
                  </table>
                </div>

                <div class="card">
                  <h2>Recommendations</h2>
                  <ul>
                    $recs
                  </ul>
                </div>
              </body>
            </html>
        """.trimIndent()
    }

    fun buildPlainSummary(
        windowStartMillis: Long,
        windowEndMillis: Long,
        avgDrainPerHour: Double?,
        peakTempC: Double?,
        worstApp: AppPowerEntry?,
    ): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val start = fmt.format(Date(windowStartMillis))
        val end = fmt.format(Date(windowEndMillis))
        return buildString {
            appendLine("Battery & Thermal Profiler report")
            appendLine("Window: $start → $end")
            appendLine()
            appendLine("Avg drain: ${avgDrainPerHour?.let { "%.2f %%/h".format(it) } ?: "N/A"}")
            appendLine("Peak temp: ${peakTempC?.let { "%.1f °C".format(it) } ?: "N/A"}")
            appendLine("Worst app: ${worstApp?.packageName ?: "N/A"}")
        }.trim()
    }

    private fun escape(s: String): String =
        s.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")

    private fun formatMs(ms: Long): String {
        val totalSeconds = (ms / 1000).coerceAtLeast(0)
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return when {
            h > 0 -> "${h}h ${m}m"
            m > 0 -> "${m}m ${s}s"
            else -> "${s}s"
        }
    }
}

