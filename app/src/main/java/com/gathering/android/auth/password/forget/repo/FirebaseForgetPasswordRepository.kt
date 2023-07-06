package com.gathering.android.auth.password.forget.repo

import com.gathering.android.common.ResponseState
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseForgetPasswordRepository @Inject constructor(
    private val auth: FirebaseAuth
) : ForgetPasswordRepository {

    override fun forgetPassword(email: String, onResponseReady: (ResponseState) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResponseReady(ResponseState.Success(Unit))
            } else {
                onResponseReady(ResponseState.Failure(Throwable("Failed to send reset password link, try again!")))
            }
        }
    }
}