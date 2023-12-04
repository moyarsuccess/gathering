package com.gathering.android.auth.repo

interface AuthRepository {
    suspend fun forgetPassword(
        email: String,
    )
    suspend fun resetPassword(
        token: String,
        password: String,
        deviceToken: String,
    )

    suspend fun signInUser(
        email: String,
        pass: String,
        deviceToken: String,
    )

    suspend fun signUpUser(
        email: String,
        pass: String,
        deviceToken: String,
    )

    suspend fun sendEmailVerification(email: String)

    suspend fun emailVerify(token: String)

    fun isUserVerified(): Boolean
}