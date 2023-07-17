package com.gathering.android.auth.verification.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.ResponseState
import com.gathering.android.common.TokenRepo
import com.gathering.android.common.UserRepo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiVerificationRepository @Inject constructor(
    private val verificationRemoteService: VerificationRemoteService,
    private val tokenRepo: TokenRepo,
    private val userRepo: UserRepo
) : VerificationRepository {

    override fun sendEmailVerification(email: String, onResponseReady: (ResponseState) -> Unit) {
        verificationRemoteService.sendEmailVerification(email = email)
            .enqueue(object : Callback<GeneralApiResponse> {
                override fun onResponse(
                    call: Call<GeneralApiResponse>,
                    response: Response<GeneralApiResponse>
                ) {
                    if (response.isSuccessful) {
                        onResponseReady(ResponseState.Success(response.body()))
                    } else {
                        onResponseReady(ResponseState.SuccessWithError(response.body()))
                    }
                }

                override fun onFailure(call: Call<GeneralApiResponse>, t: Throwable) {
                    onResponseReady(ResponseState.Failure(t))
                }
            })
    }

    override fun emailVerify(token: String, onResponseReady: (ResponseState) -> Unit) {
        verificationRemoteService.emailVerify(token)
            .enqueue(object : Callback<AuthorizedResponse> {
                override fun onResponse(
                    call: Call<AuthorizedResponse>,
                    response: Response<AuthorizedResponse>
                ) {
                    if (!response.isSuccessful) {
                        onResponseReady(ResponseState.SuccessWithError(response.body()))
                        return
                    }
                    response.body()?.also { body ->
                        userRepo.saveUser(body.user)
                        tokenRepo.saveToken(body.jwt)
                        onResponseReady(ResponseState.Success(response.body()))
                    } ?: run {
                        onResponseReady(ResponseState.SuccessWithError(response.message()))
                    }
                }

                override fun onFailure(call: Call<AuthorizedResponse>, t: Throwable) {
                    onResponseReady(ResponseState.Failure(t))
                }
            })
    }

    override fun isUserVerified(): Boolean {
        return tokenRepo.isTokenValid()
    }
}
