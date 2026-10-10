package com.oro.local.data.source

import com.oro.local.data.model.VitalsSample
import kotlinx.coroutines.flow.Flow

interface WatchDataSource {
    fun samples(): Flow<VitalsSample>
}