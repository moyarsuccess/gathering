package com.gathering.android.auth.verification.repo

import com.gathering.android.common.ResponseState

interface VerificationRepository {
    fun sendEmailVerification(email: String, onResponseReady: (ResponseState) -> Unit)
    fun emailVerify(token: String, onResponseReady: (ResponseState) -> Unit)

    fun isUserVerified(): Boolean
}

