package com.gathering.android.auth.password.forget.repo

import com.gathering.android.common.GeneralApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ForgetPasswordRemoteService {

    @GET("auth/password/forget")
    fun forgetPassword(
        @Query("email") email: String,
    ): Call<GeneralApiResponse>
}