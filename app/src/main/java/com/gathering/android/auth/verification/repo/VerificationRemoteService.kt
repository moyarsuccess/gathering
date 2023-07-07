package com.gathering.android.auth.verification.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.GeneralApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface VerificationRemoteService {
    @GET("auth/verification")
    fun sendEmailVerification(@Query("email") email: String): Call<GeneralApiResponse>

    @GET("auth/verification")
    fun emailVerify(@Query("token") token: String): Call<AuthorizedResponse>
}