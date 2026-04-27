package com.aylar.batterythermalprofiler.monitoring

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aylar.batterythermalprofiler.core.data.settings.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            val enabled = SettingsStore(appContext).startOnBootEnabled().first()
            if (enabled) BatteryMonitorService.start(appContext)
        }
    }
}

