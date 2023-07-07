package com.gathering.android.auth.password.repo

import com.gathering.android.common.ResponseState

interface PasswordRepository {

    fun forgetPassword(email: String, onResponseReady: (ResponseState) -> Unit)

    fun resetPassword(token: String, password: String, onResponseReady: (ResponseState) -> Unit)
}