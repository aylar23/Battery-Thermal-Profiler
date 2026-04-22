package com.aylar.batterythermalprofiler.core.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aylar.batterythermalprofiler.core.data.db.entity.ThermalSnapshotEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ThermalSnapshotDaoTest {
    private lateinit var db: BatteryDatabase

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, BatteryDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun thermalSnapshotDao_between_returnsInAscendingOrder() = runTest {
        val dao = db.thermalSnapshotDao()

        dao.insert(ThermalSnapshotEntity(timestampMillis = 10, thermalStatus = 1, cpuTempDeciC = 300))
        dao.insert(ThermalSnapshotEntity(timestampMillis = 20, thermalStatus = 2, cpuTempDeciC = 310))

        val list = dao.between(startMillis = 0, endMillis = 30).first()
        assertEquals(listOf(10L, 20L), list.map { it.timestampMillis })
    }
}

