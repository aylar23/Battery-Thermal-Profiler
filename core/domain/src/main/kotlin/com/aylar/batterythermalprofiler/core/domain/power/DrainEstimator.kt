package com.aylar.batterythermalprofiler.core.domain.power

import com.aylar.batterythermalprofiler.core.domain.model.AppPowerEntry

object DrainEstimator {
    /**
     * Simple linear model:
     * estimated_mAh = (foregroundMs + backgroundMs) * coefficientMahPerMs
     */
    fun estimateMah(
        foregroundMs: Long,
        backgroundMs: Long,
        coefficientMahPerMs: Double,
    ): Double = (foregroundMs + backgroundMs).coerceAtLeast(0) * coefficientMahPerMs

    fun withEstimate(
        base: AppPowerEntry,
        coefficientMahPerMs: Double,
    ): AppPowerEntry = base.copy(
        estimatedMah = estimateMah(base.foregroundMs, base.backgroundMs, coefficientMahPerMs),
    )
}

