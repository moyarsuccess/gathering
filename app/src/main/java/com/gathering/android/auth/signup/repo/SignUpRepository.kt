package com.gathering.android.auth.signup.repo

import com.gathering.android.common.ResponseState

interface SignUpRepository {
    fun signUpUser(email: String, pass: String, onResponseReady: (ResponseState<String>) -> Unit)

}