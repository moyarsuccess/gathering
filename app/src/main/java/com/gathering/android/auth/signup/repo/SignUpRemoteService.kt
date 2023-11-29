package com.gathering.android.auth.signup.repo

import com.gathering.android.common.GeneralApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SignUpRemoteService {
    @GET("auth/register")
    fun signUp(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String,
    ): Call<GeneralApiResponse>
}