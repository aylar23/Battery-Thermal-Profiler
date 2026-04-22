package com.aylar.batterythermalprofiler.core.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aylar.batterythermalprofiler.core.data.db.entity.BatterySnapshotEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BatteryDatabaseTest {
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
    fun batterySnapshotDao_insert_thenLatest_returnsInserted() = runTest {
        val dao = db.batterySnapshotDao()
        val t = 1_700_000_000_000L

        dao.insert(
            BatterySnapshotEntity(
                timestampMillis = t,
                level = 50,
                temperatureDeciC = 320,
                voltageMv = 3850,
                status = 2,
                plugged = 0,
            ),
        )

        val latest = dao.latest().first()
        assertNotNull(latest)
        assertEquals(t, latest!!.timestampMillis)
        assertEquals(50, latest.level)
    }
}

