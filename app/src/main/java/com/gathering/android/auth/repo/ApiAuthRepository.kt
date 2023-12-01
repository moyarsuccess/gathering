package com.gathering.android.auth.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.BODY_WAS_NULL
import com.gathering.android.common.EmailAlreadyInUse
import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.RESPONSE_IS_NOT_SUCCESSFUL
import com.gathering.android.common.ResponseState
import com.gathering.android.common.TokenRepo
import com.gathering.android.common.UserNotVerifiedException
import com.gathering.android.common.UserRepo
import com.gathering.android.common.WrongCredentialsException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiAuthRepository @Inject constructor(
    private val remoteService: AuthRemoteService,
    private val tokenRepo: TokenRepo,
    private val userRepo: UserRepo
) : AuthRepository {
    override fun forgetPassword(email: String, onResponseReady: (ResponseState<String>) -> Unit) {
        remoteService.forgetPassword(email).enqueue(object : Callback<GeneralApiResponse> {
            override fun onResponse(
                call: Call<GeneralApiResponse>, response: Response<GeneralApiResponse>
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

    override suspend fun forgetPassword2(email: String) {
        remoteService.forgetPassword2(email)
    }

    override fun resetPassword(
        token: String,
        password: String,
        deviceToken: String,
        onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    ) {
        remoteService.resetPassword(token, password, deviceToken)
            .enqueue(object : Callback<AuthorizedResponse> {
                override fun onResponse(
                    call: Call<AuthorizedResponse>, response: Response<AuthorizedResponse>
                ) {
                    if (!response.isSuccessful) {
                        onResponseReady(ResponseState.Failure(Exception(RESPONSE_IS_NOT_SUCCESSFUL)))
                        return
                    }
                    val body = response.body()
                    if (body == null) {
                        onResponseReady(ResponseState.Failure(Exception(BODY_WAS_NULL)))
                        return
                    }
                    val jwt = body.jwt
                    tokenRepo.saveToken(jwt)
                    userRepo.saveUser(body.user)
                    onResponseReady(ResponseState.Success(body))

                }

                override fun onFailure(call: Call<AuthorizedResponse>, t: Throwable) {
                    onResponseReady(ResponseState.Failure(t))
                }
            })
    }

    override suspend fun resetPassword2(
        token: String,
        password: String,
        deviceToken: String
    ): AuthorizedResponse {
        return remoteService.resetPassword2(token, password, deviceToken)
    }

    override fun signInUser(
        email: String,
        pass: String,
        deviceToken: String,
        onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    ) {
        remoteService.signIn(email, pass, deviceToken)
            .enqueue(object : Callback<AuthorizedResponse> {
                override fun onResponse(
                    call: Call<AuthorizedResponse>, response: Response<AuthorizedResponse>
                ) {
                    if (!response.isSuccessful) {
                        if (response.code() == BAD_REQUEST_HTTP_CODE) {
                            onResponseReady(ResponseState.Failure(WrongCredentialsException()))
                            return
                        }
                        if (response.code() == UNAUTHORIZED_HTTP_CODE) {
                            onResponseReady(ResponseState.Failure(UserNotVerifiedException()))
                            return
                        }
                        onResponseReady(ResponseState.Failure(Exception(response.message())))
                        return
                    }
                    val body = response.body()
                    if (body == null) {
                        onResponseReady(ResponseState.Failure(Exception(BODY_WAS_NULL)))
                        return
                    }
                    tokenRepo.saveToken(body.jwt)
                    userRepo.saveUser(body.user)
                    onResponseReady(ResponseState.Success(body))
                }

                override fun onFailure(call: Call<AuthorizedResponse>, t: Throwable) {
                    onResponseReady(ResponseState.Failure(t))
                }
            })
    }

    override suspend fun signInUser2(
        email: String,
        pass: String,
        deviceToken: String
    ): AuthorizedResponse {
        return remoteService.signIn2(email, pass, deviceToken)
    }

    override fun signUpUser(
        email: String,
        pass: String,
        deviceToken: String,
        onResponseReady: (ResponseState<String>) -> Unit
    ) {
        remoteService.signUp(
            email = email, password = pass, deviceToken = deviceToken
        ).enqueue(object : Callback<GeneralApiResponse> {
                override fun onResponse(
                    call: Call<GeneralApiResponse>, response: Response<GeneralApiResponse>
                ) {
                    if (!response.isSuccessful) {
                        if (response.code() == CONFLICT_HTTP_CODE) {
                            onResponseReady(ResponseState.Failure(EmailAlreadyInUse()))
                            return
                        }
                        onResponseReady(ResponseState.Failure(Exception(RESPONSE_IS_NOT_SUCCESSFUL)))
                    }
                    onResponseReady(ResponseState.Success(response.body()?.message ?: ""))
                }

                override fun onFailure(call: Call<GeneralApiResponse>, t: Throwable) {
                    onResponseReady(ResponseState.Failure(t))
                }
            })

    override suspend fun signUpUser2(email: String, pass: String, deviceToken: String) {
        remoteService.signIn2(email, pass, deviceToken)
    }

    override fun sendEmailVerification(
        email: String, onResponseReady: (ResponseState<String>) -> Unit
    ) {
        remoteService.sendEmailVerification(email = email)
            .enqueue(object : Callback<GeneralApiResponse> {
                override fun onResponse(
                    call: Call<GeneralApiResponse>, response: Response<GeneralApiResponse>
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

    override suspend fun sendEmailVerification2(email: String) {
        remoteService.sendEmailVerification2(email)
    }

    override fun emailVerify(
        token: String, onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    ) {
        remoteService.emailVerify(token).enqueue(object : Callback<AuthorizedResponse> {
                override fun onResponse(
                    call: Call<AuthorizedResponse>, response: Response<AuthorizedResponse>
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

    override suspend fun emailVerify2(token: String): AuthorizedResponse {
        return remoteService.emailVerify2(token)
    }

    override fun isUserVerified(): Boolean {
        return tokenRepo.isTokenValid()
    }

    companion object {
        private const val BAD_REQUEST_HTTP_CODE = 400
        private const val UNAUTHORIZED_HTTP_CODE = 401
        private const val CONFLICT_HTTP_CODE = 409
    }
}