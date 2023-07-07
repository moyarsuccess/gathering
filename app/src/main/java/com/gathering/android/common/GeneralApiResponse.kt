package com.gathering.android.common

data class GeneralApiResponse(
    override val code: Int,
    override val message: String?,
) : ApiResponse
