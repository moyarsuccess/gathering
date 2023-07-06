package com.gathering.android.auth.password.reset.repo

import com.gathering.android.common.GeneralApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ResetPasswordRemoteService {

    @GET("auth/password/reset")
    fun resetPassword(
        @Query("token") token: String,
        @Query("password") password: String,
    ): Call<GeneralApiResponse>
}