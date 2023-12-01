package com.gathering.android.auth.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.ResponseState

interface AuthRepository {
    fun forgetPassword(
        email: String,
        onResponseReady: (ResponseState<String>) -> Unit
    )

    suspend fun forgetPassword2(email: String)

    fun resetPassword(
        token: String,
        password: String,
        deviceToken: String,
        onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    )

    suspend fun resetPassword2(
        token: String,
        password: String,
        deviceToken: String,
    ): AuthorizedResponse

    fun signInUser(
        email: String,
        pass: String,
        deviceToken: String,
        onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    )

    suspend fun signInUser2(
        email: String,
        pass: String,
        deviceToken: String,
    ): AuthorizedResponse

    fun signUpUser(
        email: String,
        pass: String,
        deviceToken: String,
        onResponseReady: (ResponseState<String>) -> Unit
    )

    suspend fun signUpUser2(
        email: String,
        pass: String,
        deviceToken: String,
    )

    fun sendEmailVerification(email: String, onResponseReady: (ResponseState<String>) -> Unit)

    suspend fun sendEmailVerification2(email: String)

    fun emailVerify(token: String, onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit)

    suspend fun emailVerify2(token: String): AuthorizedResponse

    fun isUserVerified(): Boolean

}