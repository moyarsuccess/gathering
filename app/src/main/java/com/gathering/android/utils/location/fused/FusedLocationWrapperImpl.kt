package com.gathering.android.utils.location.fused

import android.annotation.SuppressLint
import com.gathering.android.common.toEventLocation
import com.gathering.android.event.model.EventLocation
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FusedLocationWrapperImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : FusedLocationWrapper {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): EventLocation = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result.toEventLocation())
                } else {
                    continuation.resumeWithException(IllegalStateException("Last known location was not acquired!"))
                }
            }
        }
    }
}