package com.aylar.batterythermalprofiler.feature.report

enum class ReportRange(val label: String, val durationMillis: Long) {
    Last6h("6h", 6L * 60 * 60 * 1000),
    Last24h("24h", 24L * 60 * 60 * 1000),
    Last7d("7d", 7L * 24 * 60 * 60 * 1000),
}

data class ReportUiState(
    val isGenerating: Boolean = false,
    val errorMessage: String? = null,
    val range: ReportRange = ReportRange.Last24h,
    val html: String? = null,
    val plainSummary: String? = null,
    val pdfUriString: String? = null,
)

