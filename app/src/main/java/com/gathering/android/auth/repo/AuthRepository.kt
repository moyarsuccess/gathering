package com.gathering.android.auth.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.ResponseState

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

    fun sendEmailVerification(email: String, onResponseReady: (ResponseState<String>) -> Unit)
    suspend fun sendEmailVerification1(email: String)

    fun emailVerify(token: String, onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit)
    suspend fun emailVerify1(token: String)

    fun isUserVerified(): Boolean
}