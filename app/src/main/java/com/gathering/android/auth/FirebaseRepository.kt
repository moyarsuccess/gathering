package com.gathering.android.auth

import android.util.Log
import com.gathering.android.home.FilterDialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class FirebaseRepository {
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
}