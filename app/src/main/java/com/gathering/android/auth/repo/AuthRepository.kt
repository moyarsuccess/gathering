package com.gathering.android.auth.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.ResponseState

interface AuthRepository {
    fun forgetPassword(
        email: String,
        onResponseReady: (ResponseState<String>) -> Unit
    )

    fun resetPassword(
        token: String,
        password: String,
        deviceToken: String,
        onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    )

    fun signInUser(
        email: String,
        pass: String,
        deviceToken: String,
        onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    )

    fun signUpUser(
        email: String,
        pass: String,
        deviceToken: String,
        onResponseReady: (ResponseState<String>) -> Unit
    )

    fun sendEmailVerification(email: String, onResponseReady: (ResponseState<String>) -> Unit)

    fun emailVerify(token: String, onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit)

    fun isUserVerified(): Boolean

}