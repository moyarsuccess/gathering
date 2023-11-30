package com.gathering.android.auth.password.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.BODY_WAS_NULL
import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.RESPONSE_IS_NOT_SUCCESSFUL
import com.gathering.android.common.ResponseState
import com.gathering.android.common.TokenRepo
import com.gathering.android.common.UserRepo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiPasswordRepository @Inject constructor(
    private val passwordRemoteService: PasswordRemoteService,
    private val tokenRepo: TokenRepo,
    private val userRepo: UserRepo,
) : PasswordRepository {

    override fun forgetPassword(email: String, onResponseReady: (ResponseState<String>) -> Unit) {
        passwordRemoteService.forgetPassword(email).enqueue(object : Callback<GeneralApiResponse> {
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

    override fun resetPassword(
        token: String,
        password: String,
        deviceToken: String,
        onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    ) {
        passwordRemoteService.resetPassword(token, password, deviceToken)
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
}