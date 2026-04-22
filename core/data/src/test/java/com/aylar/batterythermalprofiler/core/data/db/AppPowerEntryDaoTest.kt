package com.aylar.batterythermalprofiler.core.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aylar.batterythermalprofiler.core.data.db.entity.AppPowerEntryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppPowerEntryDaoTest {
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
    fun appPowerEntryDao_topByEstimatedMah_ordersDescending() = runTest {
        val dao = db.appPowerEntryDao()
        val start = 1000L
        val end = 2000L

        dao.upsertAll(
            listOf(
                AppPowerEntryEntity(
                    packageName = "a",
                    uid = 1,
                    windowStartMillis = start,
                    windowEndMillis = end,
                    foregroundMs = 10,
                    backgroundMs = 20,
                    wakelocksHeld = 0,
                    estimatedMah = 1.0,
                ),
                AppPowerEntryEntity(
                    packageName = "b",
                    uid = 2,
                    windowStartMillis = start,
                    windowEndMillis = end,
                    foregroundMs = 10,
                    backgroundMs = 20,
                    wakelocksHeld = 0,
                    estimatedMah = 5.0,
                ),
            ),
        )

        val top = dao.topByEstimatedMah(start, end, limit = 10).first()
        assertEquals(listOf("b", "a"), top.map { it.packageName })
    }
}

