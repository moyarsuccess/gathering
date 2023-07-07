package com.gathering.android.auth.password.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.ResponseState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiPasswordRepository @Inject constructor(
    private val passwordRemoteService: PasswordRemoteService,
) : PasswordRepository {

    override fun forgetPassword(email: String, onResponseReady: (ResponseState) -> Unit) {
        passwordRemoteService.forgetPassword(email)
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

    override fun resetPassword(
        token: String,
        password: String,
        onResponseReady: (ResponseState) -> Unit
    ) {
        passwordRemoteService.resetPassword(token, password)
            .enqueue(object : Callback<AuthorizedResponse> {
                override fun onResponse(
                    call: Call<AuthorizedResponse>,
                    response: Response<AuthorizedResponse>
                ) {
                    if (response.isSuccessful) {
                        // TODO Save the JWT in shared pref to be used in future API calls
                        onResponseReady(ResponseState.Success(response.body()))
                    } else {
                        onResponseReady(ResponseState.SuccessWithError(response.body()))
                    }
                }

                override fun onFailure(call: Call<AuthorizedResponse>, t: Throwable) {
                    onResponseReady(ResponseState.Failure(t))
                }
            })
    }
}