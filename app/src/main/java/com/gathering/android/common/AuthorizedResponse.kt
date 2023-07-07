package com.gathering.android.common

import com.gathering.android.auth.model.User

data class AuthorizedResponse(
    override val code: Int,
    override val message: String?,
    val jwt: String?,
    val user: User?
) : ApiResponse
