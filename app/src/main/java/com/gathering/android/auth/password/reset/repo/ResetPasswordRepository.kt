package com.gathering.android.auth.password.reset.repo

import com.gathering.android.common.ResponseState

interface ResetPasswordRepository {

    fun resetPassword(token: String, password: String, onResponseReady: (ResponseState) -> Unit)
}