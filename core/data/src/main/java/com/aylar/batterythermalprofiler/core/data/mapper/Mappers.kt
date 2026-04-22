package com.aylar.batterythermalprofiler.core.data.mapper

import com.aylar.batterythermalprofiler.core.data.db.entity.AppPowerEntryEntity
import com.aylar.batterythermalprofiler.core.data.db.entity.BatterySnapshotEntity
import com.aylar.batterythermalprofiler.core.data.db.entity.ThermalSnapshotEntity
import com.aylar.batterythermalprofiler.core.data.db.entity.WakelockEventEntity
import com.aylar.batterythermalprofiler.core.domain.model.AppPowerEntry
import com.aylar.batterythermalprofiler.core.domain.model.BatterySnapshot
import com.aylar.batterythermalprofiler.core.domain.model.ThermalSnapshot
import com.aylar.batterythermalprofiler.core.domain.model.WakelockEvent

internal fun BatterySnapshotEntity.toDomain(): BatterySnapshot =
    BatterySnapshot(
        timestampMillis = timestampMillis,
        level = level,
        temperatureDeciC = temperatureDeciC,
        voltageMv = voltageMv,
        status = status,
        plugged = plugged,
    )

internal fun BatterySnapshot.toEntity(): BatterySnapshotEntity =
    BatterySnapshotEntity(
        timestampMillis = timestampMillis,
        level = level,
        temperatureDeciC = temperatureDeciC,
        voltageMv = voltageMv,
        status = status,
        plugged = plugged,
    )

internal fun AppPowerEntryEntity.toDomain(): AppPowerEntry =
    AppPowerEntry(
        packageName = packageName,
        uid = uid,
        windowStartMillis = windowStartMillis,
        windowEndMillis = windowEndMillis,
        foregroundMs = foregroundMs,
        backgroundMs = backgroundMs,
        wakelocksHeld = wakelocksHeld,
        estimatedMah = estimatedMah,
    )

internal fun AppPowerEntry.toEntity(): AppPowerEntryEntity =
    AppPowerEntryEntity(
        packageName = packageName,
        uid = uid,
        windowStartMillis = windowStartMillis,
        windowEndMillis = windowEndMillis,
        foregroundMs = foregroundMs,
        backgroundMs = backgroundMs,
        wakelocksHeld = wakelocksHeld,
        estimatedMah = estimatedMah,
    )

internal fun ThermalSnapshotEntity.toDomain(): ThermalSnapshot =
    ThermalSnapshot(
        timestampMillis = timestampMillis,
        thermalStatus = thermalStatus,
        cpuTempDeciC = cpuTempDeciC,
    )

internal fun ThermalSnapshot.toEntity(): ThermalSnapshotEntity =
    ThermalSnapshotEntity(
        timestampMillis = timestampMillis,
        thermalStatus = thermalStatus,
        cpuTempDeciC = cpuTempDeciC,
    )

internal fun WakelockEventEntity.toDomain(): WakelockEvent =
    WakelockEvent(
        packageName = packageName,
        tag = tag,
        acquiredAtMillis = acquiredAtMillis,
        durationMs = durationMs,
    )

internal fun WakelockEvent.toEntity(): WakelockEventEntity =
    WakelockEventEntity(
        packageName = packageName,
        tag = tag,
        acquiredAtMillis = acquiredAtMillis,
        durationMs = durationMs,
    )

