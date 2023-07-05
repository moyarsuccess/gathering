package com.gathering.android.auth.signin.repo

import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.ResponseState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiSignInRepository @Inject constructor(
    private val signInRemoteService: SignInRemoteService
) : SignInRepository {

    override fun signInUser(
        email: String,
        pass: String,
        onResponseReady: (ResponseState) -> Unit
    ) {
        signInRemoteService.signIn(email, pass).enqueue(object : Callback<GeneralApiResponse> {
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