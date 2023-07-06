package com.gathering.android.auth.sendVerificationEmail

import com.gathering.android.common.ResponseState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class FireBaseSendEmailVerificationRepository
@Inject constructor(
    private val auth: FirebaseAuth
) : SendEmailVerificationRepository {

    override fun sendEmailVerification(email: String, onResponseReady: (ResponseState) -> Unit) {
        val user = Firebase.auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResponseReady(ResponseState.Success(Unit))
            } else {
                onResponseReady(ResponseState.Failure(Exception("Failed to send Email Verification, try again!")))
            }
        }
    }
}