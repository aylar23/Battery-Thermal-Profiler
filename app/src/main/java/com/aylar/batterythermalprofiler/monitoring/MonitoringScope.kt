package com.aylar.batterythermalprofiler.monitoring

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

internal object MonitoringScope : CoroutineScope {
    override val coroutineContext = SupervisorJob() + Dispatchers.IO
}

internal fun MonitoringScope.cancelAll() {
    coroutineContext.cancel()
}

