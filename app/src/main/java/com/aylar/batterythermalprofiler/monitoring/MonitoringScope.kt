package com.aylar.batterythermalprofiler.monitoring

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal object MonitoringScope : CoroutineScope {
    override val coroutineContext = SupervisorJob() + Dispatchers.IO
}

