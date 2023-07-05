package com.gathering.android.auth.signin.repo

import com.gathering.android.common.ResponseState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class FirebaseSignInRepository @Inject constructor(
    private val auth: FirebaseAuth
) : SignInRepository {

    override fun signInUser(email: String, pass: String, onResponseReady: (ResponseState) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                if (user?.isEmailVerified == true) {
                    onResponseReady(ResponseState.Success(Unit))
                } else {
                    onResponseReady(ResponseState.Failure(Throwable("Account Not Verified Please Check Your Inbox For Verification Email")))
                }
            } else {
                onResponseReady(ResponseState.Failure(Throwable("Authentication failed")))
            }
        }.addOnFailureListener { error ->
            onResponseReady(ResponseState.Failure(error))
        }
    }
}