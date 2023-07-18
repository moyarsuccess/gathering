package com.gathering.android.auth.signin.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.common.TokenRepo
import com.gathering.android.common.UserNotVerifiedException
import com.gathering.android.common.WrongCredentialsException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiSignInRepository @Inject constructor(
    private val signInRemoteService: SignInRemoteService,
    private val tokenRepo: TokenRepo
) : SignInRepository {

    override fun signInUser(
        email: String,
        pass: String,
        onResponseReady: (ResponseState) -> Unit
    ) {
        signInRemoteService.signIn(email, pass).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    val jwt = response.body()
                    tokenRepo.saveToken(jwt)
                    onResponseReady(ResponseState.Success(response.body()))
                } else {
                    if (response.code() == 400) {
                        onResponseReady(ResponseState.Failure(WrongCredentialsException()))
                    } else if (response.code() == 401) {
                        onResponseReady(ResponseState.Failure(UserNotVerifiedException()))
                    }
                    onResponseReady(ResponseState.Failure(Exception(response.body())))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onResponseReady(ResponseState.Failure(t))
            }
        })
    }
}