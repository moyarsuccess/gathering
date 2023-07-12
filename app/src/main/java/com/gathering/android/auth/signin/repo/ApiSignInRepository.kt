package com.gathering.android.auth.signin.repo

import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.ResponseState
import com.gathering.android.common.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiSignInRepository @Inject constructor(
    private val signInRemoteService: SignInRemoteService,
    private val tokenManager: TokenManager
) : SignInRepository {

    override fun signInUser(
        email: String,
        pass: String,
        onResponseReady: (ResponseState) -> Unit
    ) {
        signInRemoteService.signIn(email, pass).enqueue(object : Callback<AuthorizedResponse> {
            override fun onResponse(
                call: Call<AuthorizedResponse>,
                response: Response<AuthorizedResponse>
            ) {
                if (response.isSuccessful) {
                    if(response.body()?.code == 200) {
                        val jwt = response.body()?.jwt
                        tokenManager.saveToken(jwt)
                        onResponseReady(ResponseState.Success(response.body()))
                    }
                    else {
                        onResponseReady(ResponseState.SuccessWithError(response.body()))
                    }
                } else
                {
                    onResponseReady(ResponseState.Failure(Exception(response.body()?.message)))
                }
            }

            override fun onFailure(call: Call<AuthorizedResponse>, t: Throwable) {
                onResponseReady(ResponseState.Failure(t))
            }
        })
    }
}