package com.gathering.android.common

import com.gathering.android.auth.model.User

data class UpdateProfileResponse(
    override val message: String?,
    val user: User,
) : ApiResponse