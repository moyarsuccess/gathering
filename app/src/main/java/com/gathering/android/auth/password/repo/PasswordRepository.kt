package com.gathering.android.auth.password.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.ResponseState

interface PasswordRepository {

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
}