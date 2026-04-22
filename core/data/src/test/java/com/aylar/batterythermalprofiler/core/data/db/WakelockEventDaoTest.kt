package com.aylar.batterythermalprofiler.core.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aylar.batterythermalprofiler.core.data.db.entity.WakelockEventEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WakelockEventDaoTest {
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
    fun wakelockEventDao_recent_returnsNewestFirst() = runTest {
        val dao = db.wakelockEventDao()

        dao.insert(WakelockEventEntity(packageName = "p", tag = "t1", acquiredAtMillis = 10, durationMs = 1))
        dao.insert(WakelockEventEntity(packageName = "p", tag = "t2", acquiredAtMillis = 20, durationMs = 1))

        val list = dao.recent(limit = 10).first()
        assertEquals(listOf(20L, 10L), list.map { it.acquiredAtMillis })
    }
}

