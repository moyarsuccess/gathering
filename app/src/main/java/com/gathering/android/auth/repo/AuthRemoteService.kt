package com.gathering.android.auth.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.GeneralApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthRemoteService {
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

    @GET("auth/login")
    fun signIn(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): Call<AuthorizedResponse>

    @GET("auth/register")
    suspend fun signUp(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): GeneralApiResponse

    @GET("auth/verification/email")
    fun sendEmailVerification(@Query("email") email: String): Call<GeneralApiResponse>

    @GET("auth/verification")
    fun emailVerify(@Query("token") token: String): Call<AuthorizedResponse>
}