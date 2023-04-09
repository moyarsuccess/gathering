package com.gathering.android.signup.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class AuthRepository @Inject constructor() {

    private lateinit var auth: FirebaseAuth

    fun signUpUser(email: String, pass: String, onResponseReady: (ResponseState) -> Unit) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    onResponseReady(ResponseState.Success(user.toUser()))
                } else {
                    onResponseReady(ResponseState.Failure("Authentication failed"))
                }
            }
            .addOnFailureListener { error ->
                onResponseReady(ResponseState.Failure(error.toString()))
            }
    }

    fun isSignedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    private fun FirebaseUser?.toUser(): User {
        return User(
            uId = this?.uid,
            displayName = this?.displayName,
            phoneNumber = this?.phoneNumber,
            photoUrl = this?.photoUrl.toString(),
            isEmailVerified = this?.isEmailVerified,
        )
    }
}