package com.gathering.android.auth.signup.repo

import com.gathering.android.common.EmailAlreadyInUse
import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.RESPONSE_IS_NOT_SUCCESSFUL
import com.gathering.android.common.ResponseState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiSignUpRepository @Inject constructor(
    private val signUpRemoteService: SignUpRemoteService,
) : SignUpRepository {
    override fun signUpUser(
        email: String,
        pass: String,
        deviceToken: String,
        onResponseReady: (ResponseState<String>) -> Unit
    ) {
        signUpRemoteService.signUp(
            email = email,
            password = pass,
            deviceToken = deviceToken
        )
            .enqueue(object : Callback<GeneralApiResponse> {
                override fun onResponse(
                    call: Call<GeneralApiResponse>,
                    response: Response<GeneralApiResponse>
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
    }

    companion object {
        private const val CONFLICT_HTTP_CODE = 409
    }
}