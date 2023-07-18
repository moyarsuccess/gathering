package com.gathering.android.auth.signin.repo

import com.gathering.android.common.AuthorizedResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SignInRemoteService {
    @GET("auth/login")
    fun signIn(
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<String>
}