package com.aylar.batterythermalprofiler

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.aylar.batterythermalprofiler.work.WorkScheduler

@HiltAndroidApp
class BatteryThermalProfilerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        WorkScheduler.schedulePeriodicAppPowerCollection(this)
    }
}

