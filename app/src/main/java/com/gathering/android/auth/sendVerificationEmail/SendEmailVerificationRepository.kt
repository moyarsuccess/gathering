package com.gathering.android.auth.sendVerificationEmail

import com.gathering.android.common.ResponseState

interface SendEmailVerificationRepository {
    fun sendEmailVerification(email: String, onResponseReady: (ResponseState) -> Unit)
    fun emailVerify(token: String, onResponseReady: (ResponseState) -> Unit)
}

