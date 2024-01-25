package com.gathering.android.auth.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.GeneralApiResponse

interface AuthRepository {
    suspend fun forgetPassword(
        email: String,
    ): GeneralApiResponse

    suspend fun resetPassword(
        token: String,
        password: String,
        deviceToken: String,
    ): AuthorizedResponse

    suspend fun signInUser(
        email: String,
        pass: String,
        deviceToken: String,
    ): AuthorizedResponse

    suspend fun signUpUser(
        email: String,
        pass: String,
        deviceToken: String,
    ): GeneralApiResponse

    suspend fun sendEmailVerification(email: String): GeneralApiResponse

    suspend fun emailVerify(token: String): AuthorizedResponse

    fun isUserVerified(): Boolean
}