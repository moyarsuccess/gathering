package com.gathering.android.auth.password.reset.repo

import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.ResponseState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiResetPasswordRepository @Inject constructor(
    private val resetPasswordRemoteService: ResetPasswordRemoteService
) : ResetPasswordRepository {

    override fun resetPassword(
        token: String,
        password: String,
        onResponseReady: (ResponseState) -> Unit
    ) {
        resetPasswordRemoteService.resetPassword(token, password)
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
}