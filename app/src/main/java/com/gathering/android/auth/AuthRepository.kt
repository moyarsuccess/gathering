package com.gathering.android.auth

import com.gathering.android.auth.model.ResponseState
import com.gathering.android.auth.model.SignInFailed
import com.gathering.android.auth.model.User
import com.gathering.android.auth.model.VerificationNeeded
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class AuthRepository @Inject constructor() {

    private lateinit var auth: FirebaseAuth

    fun signInUser(email: String, pass: String, onResponseReady: (ResponseState) -> Unit) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                if (user?.isEmailVerified == true) {
                    onResponseReady(ResponseState.Success(user.toUser()))
                } else {
                    onResponseReady(ResponseState.Failure(VerificationNeeded("Account Not Verified Please Check Your Inbox For Verification Email")))
                }
            } else {
                onResponseReady(ResponseState.Failure(SignInFailed("Authentication failed")))
            }
        }.addOnFailureListener { error ->
            onResponseReady(ResponseState.Failure(SignInFailed(error.toString())))
        }
    }

    fun signUpUser(email: String, pass: String, onResponseReady: (ResponseState) -> Unit) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                onResponseReady(ResponseState.Success(user.toUser()))
            } else {
                onResponseReady(ResponseState.Failure(Exception("SignUp failed")))
            }
        }.addOnFailureListener { error ->
            onResponseReady(ResponseState.Failure(error))
        }
    }

    fun resetPassword(email: String, onResponseReady: (ResponseState) -> Unit) {
        auth = Firebase.auth
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                onResponseReady(ResponseState.Success(user.toUser()))
            } else {
                onResponseReady(ResponseState.Failure(Exception("Failed to send reset password link, try again!")))
            }
        }
    }

    fun sendEmailVerification(onResponseReady: (ResponseState) -> Unit) {
        val user = Firebase.auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResponseReady(ResponseState.Success(user.toUser()))
            } else {
                onResponseReady(ResponseState.Failure(Exception("Failed to send Email Verification, try again!")))
            }
        }
    }

    fun isSignedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun isUserVerified(): Boolean {
        Firebase.auth.currentUser?.reload()
        return Firebase.auth.currentUser?.isEmailVerified == true
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