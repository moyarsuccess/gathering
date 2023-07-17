package com.gathering.android.common

import com.gathering.android.auth.model.User

data class AuthorizedResponse(
    override val message: String? = null,
    val jwt: String,
    val user: User
) : ApiResponse
