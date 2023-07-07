package com.gathering.android.auth.verification.repo

import com.gathering.android.common.ResponseState

interface VerificationRepository {
    fun sendEmailVerification(email: String, onResponseReady: (ResponseState) -> Unit)
    fun emailVerify(token: String, onResponseReady: (ResponseState) -> Unit)

    // TODO Check if the JWT exists the user is logged in, if not the user needs to sign in
    fun isUserVerified(): Boolean
}

