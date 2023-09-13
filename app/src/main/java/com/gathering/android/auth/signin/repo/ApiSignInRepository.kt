package com.gathering.android.auth.signin.repo

import com.gathering.android.common.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiSignInRepository @Inject constructor(
    private val signInRemoteService: SignInRemoteService,
    private val tokenRepo: TokenRepo,
    private val userRepo: UserRepo
) : SignInRepository {

    override fun signInUser(
        email: String,
        pass: String,
        onResponseReady: (ResponseState<AuthorizedResponse>) -> Unit
    ) {
        signInRemoteService.signIn(email, pass).enqueue(object : Callback<AuthorizedResponse> {
            override fun onResponse(
                call: Call<AuthorizedResponse>,
                response: Response<AuthorizedResponse>
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

    companion object {
        private const val BAD_REQUEST_HTTP_CODE = 400
        private const val UNAUTHORIZED_HTTP_CODE = 401
    }
}