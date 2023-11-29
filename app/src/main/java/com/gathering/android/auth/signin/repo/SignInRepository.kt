package com.gathering.android.auth.signin.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.ResponseState

interface SignInRepository {
    fun signInUser(
        email: String,
        pass: String,
        deviceToken: String,
        onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    )
}
