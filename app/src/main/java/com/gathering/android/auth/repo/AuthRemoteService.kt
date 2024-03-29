package com.gathering.android.auth.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.GeneralApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthRemoteService {

    @GET("auth/password/forget")
    suspend fun forgetPassword(
        @Query("email") email: String,
    ): GeneralApiResponse

    @GET("auth/password/reset")
    suspend fun resetPassword(
        @Query("token") token: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): AuthorizedResponse

    @GET("auth/login")
    suspend fun signIn(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): AuthorizedResponse

    @GET("auth/register")
    suspend fun signUp(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): GeneralApiResponse

    @GET("auth/verification/email")
    suspend fun sendEmailVerification(@Query("email") email: String): GeneralApiResponse

    @GET("auth/verification")
    suspend fun emailVerify(@Query("token") token: String): AuthorizedResponse
}