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

    @GET("auth/password/forget")
    suspend fun forgetPassword2(
        @Query("email") email: String,
    ): GeneralApiResponse

    @GET("auth/password/reset")
    fun resetPassword(
        @Query("token") token: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): Call<AuthorizedResponse>

    @GET("auth/password/reset")
    suspend fun resetPassword2(
        @Query("token") token: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): AuthorizedResponse

    @GET("auth/login")
    fun signIn(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): Call<AuthorizedResponse>

    @GET("auth/login")
    suspend fun signIn2(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): AuthorizedResponse

    @GET("auth/register")
    fun signUp(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): Call<GeneralApiResponse>

    @GET("auth/register")
    suspend fun signUp2(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): GeneralApiResponse

    @GET("auth/verification/email")
    fun sendEmailVerification(@Query("email") email: String): Call<GeneralApiResponse>

    @GET("auth/verification/email")
    suspend fun sendEmailVerification2(@Query("email") email: String): GeneralApiResponse

    @GET("auth/verification")
    fun emailVerify(@Query("token") token: String): Call<AuthorizedResponse>

    @GET("auth/verification")
    suspend fun emailVerify2(@Query("token") token: String): AuthorizedResponse
}