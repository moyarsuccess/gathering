package com.gathering.android.auth.verification.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.RESPONSE_IS_NOT_SUCCESSFUL
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

    override fun sendEmailVerification(
        email: String,
        onResponseReady: (ResponseState<String>) -> Unit
    ) {
        verificationRemoteService.sendEmailVerification(email = email)
            .enqueue(object : Callback<GeneralApiResponse> {
                override fun onResponse(
                    call: Call<GeneralApiResponse>,
                    response: Response<GeneralApiResponse>
                ) {
                    if (!response.isSuccessful) {
                        onResponseReady(ResponseState.Failure(Exception(RESPONSE_IS_NOT_SUCCESSFUL)))
                        return
                    }
                    onResponseReady(ResponseState.Success(response.body()?.message ?: ""))
                }

                override fun onFailure(call: Call<GeneralApiResponse>, t: Throwable) {
                    onResponseReady(ResponseState.Failure(t))
                }
            })
    }

    override fun emailVerify(
        token: String,
        onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    ) {
        verificationRemoteService.emailVerify(token)
            .enqueue(object : Callback<AuthorizedResponse> {
                override fun onResponse(
                    call: Call<AuthorizedResponse>,
                    response: Response<AuthorizedResponse>
                ) {
                    if (!response.isSuccessful) {
                        onResponseReady(
                            ResponseState.Failure(
                                Exception(
                                    RESPONSE_IS_NOT_SUCCESSFUL
                                )
                            )
                        )
                        return
                    }
                    response.body()?.also { body ->
                        userRepo.saveUser(body.user)
                        tokenRepo.saveToken(body.jwt)
                        onResponseReady(ResponseState.Success(body))
                    } ?: run {
                        onResponseReady(
                            ResponseState.Failure(
                                Exception(
                                    RESPONSE_IS_NOT_SUCCESSFUL
                                )
                            )
                        )
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
