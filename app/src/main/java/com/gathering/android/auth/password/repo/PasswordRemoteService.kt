package com.gathering.android.auth.password.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.GeneralApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PasswordRemoteService {

    @GET("auth/password/forget")
    fun forgetPassword(
        @Query("email") email: String,
    ): Call<GeneralApiResponse>

    @GET("auth/password/reset")
    fun resetPassword(
        @Query("token") token: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): Call<AuthorizedResponse>
}