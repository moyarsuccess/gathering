package com.gathering.android.auth.verification.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.ResponseState

interface VerificationRepository {
    fun sendEmailVerification(email: String, onResponseReady: (ResponseState<String>) -> Unit)

    fun emailVerify(token: String, onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit)

    fun isUserVerified(): Boolean
}

