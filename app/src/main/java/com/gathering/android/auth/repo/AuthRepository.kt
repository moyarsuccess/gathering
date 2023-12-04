package com.gathering.android.auth.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.ResponseState

interface AuthRepository {
    fun forgetPassword(
        email: String, onResponseReady: (ResponseState<String>) -> Unit
    )

    suspend fun forgetPassword1(
        email: String,
    )

    fun resetPassword(
        token: String,
        password: String,
        deviceToken: String,
        onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    )

    suspend fun resetPassword1(
        token: String,
        password: String,
        deviceToken: String,
    )

    fun signInUser(
        email: String,
        pass: String,
        deviceToken: String,
        onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    )

    suspend fun signInUser1(
        email: String,
        pass: String,
        deviceToken: String,
    )

    fun signUpUser(
        email: String,
        pass: String,
        deviceToken: String,
        onResponseReady: (ResponseState<String>) -> Unit
    )

    suspend fun signUpUser1(
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