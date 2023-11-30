package com.gathering.android.notif

import android.util.Log
import com.gathering.android.home.FilterDialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseRepository @Inject constructor(
    private val firebaseDeviceTokenChangeService: FirebaseDeviceTokenChangeService
) {
    suspend fun getDeviceToken(): String? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(
                        FilterDialogFragment.TAG,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    continuation.resume(null)
                    return@OnCompleteListener
                }
                continuation.resume(task.result)
            })
        }
    }


    suspend fun deviceTokenChanged(deviceToken: String) {
        val response = firebaseDeviceTokenChangeService.deviceTokenChanged(
            deviceToken = deviceToken
        )
        Log.d("FirebaseRepository", response.message ?: "")
    }
}