package com.gathering.android.auth.signup.repo

import com.gathering.android.common.ResponseState
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseSignUpRepository @Inject constructor(
    private val auth: FirebaseAuth
) : SignUpRepository {
    override fun signUpUser(email: String, pass: String, onResponseReady: (ResponseState) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResponseReady(ResponseState.Success(Unit))
            } else {
                onResponseReady(ResponseState.Failure(Exception("SignUp failed")))
            }
        }.addOnFailureListener { error ->
            onResponseReady(ResponseState.Failure(error))
        }
    }

}