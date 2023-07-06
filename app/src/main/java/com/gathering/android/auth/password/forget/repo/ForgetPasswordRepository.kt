package com.gathering.android.auth.password.forget.repo

import com.gathering.android.common.ResponseState

interface ForgetPasswordRepository {

    fun forgetPassword(email: String, onResponseReady: (ResponseState) -> Unit)
}