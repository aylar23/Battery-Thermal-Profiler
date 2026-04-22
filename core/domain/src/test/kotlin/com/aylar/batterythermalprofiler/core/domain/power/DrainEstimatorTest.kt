package com.aylar.batterythermalprofiler.core.domain.power

import org.junit.Assert.assertEquals
import org.junit.Test

class DrainEstimatorTest {
    @Test
    fun estimateMah_isLinear() {
        val coeff = 0.001
        assertEquals(2.0, DrainEstimator.estimateMah(1000, 1000, coeff), 1e-9)
    }
}

