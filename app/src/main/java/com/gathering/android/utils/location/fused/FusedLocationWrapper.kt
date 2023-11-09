package com.gathering.android.utils.location.fused

import com.gathering.android.event.model.EventLocation

interface FusedLocationWrapper {
    suspend fun getCurrentLocation(): EventLocation
}