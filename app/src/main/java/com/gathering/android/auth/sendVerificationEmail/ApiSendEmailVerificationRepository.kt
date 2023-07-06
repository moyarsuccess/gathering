package com.gathering.android.auth.sendVerificationEmail

import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.ResponseState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import javax.inject.Inject

class ApiSendEmailVerificationRepository @Inject constructor(
    private val sendEmailVerificationRemoteService: SendEmailVerificationRemoteService
) : SendEmailVerificationRepository {

    override fun sendEmailVerification(email: String, onResponseReady: (ResponseState) -> Unit) {
        sendEmailVerificationRemoteService.sendEmailVerification(email = email)
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
        sendEmailVerificationRemoteService.emailVerify(token)
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
