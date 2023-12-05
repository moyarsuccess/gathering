package com.gathering.android.auth.repo

import com.gathering.android.auth.AuthException
import com.gathering.android.common.TokenRepo
import com.gathering.android.common.UserRepo
import retrofit2.HttpException
import javax.inject.Inject

class ApiAuthRepository @Inject constructor(
    private val remoteService: AuthRemoteService,
    private val tokenRepo: TokenRepo,
    private val userRepo: UserRepo
) : AuthRepository {
    override suspend fun forgetPassword(email: String) {
        remoteService.forgetPassword(email = email)
    }

    override suspend fun resetPassword(token: String, password: String, deviceToken: String) {
        remoteService.resetPassword(
            password = password,
            deviceToken = deviceToken,
            token = token
        )
    }

    override suspend fun signInUser(
        email: String,
        pass: String,
        deviceToken: String
    ) {
        try {
            val response = remoteService.signIn(email, pass, deviceToken)
            tokenRepo.saveToken(response.jwt)
            userRepo.saveUser(response.user)
        } catch (e: HttpException) {
            val throwable = when (e.code()) {
                UNAUTHORIZED_HTTP_CODE -> AuthException.UserNotVerifiedException
                WRONG_CREDENTIAL_HTTP_CODE -> AuthException.WrongCredentialsException
                else -> AuthException.General(e.code())
            }
            throw throwable
        }
    }

    override suspend fun signUpUser(email: String, pass: String, deviceToken: String) {
        try {
            remoteService.signUp(
                email = email,
                password = pass,
                deviceToken = deviceToken,
            )
        } catch (e: HttpException) {
            val throwable = when (e.code()) {
                CONFLICT_HTTP_CODE -> AuthException.EmailAlreadyInUseException
                else -> AuthException.General(e.code())
            }
            throw throwable
        }
    }

    override suspend fun sendEmailVerification(email: String) {
        try {
            remoteService.sendEmailVerification(
                email = email,
            )
        } catch (e: HttpException) {
            val throwable = when (e.code()) {
                CAN_NOT_REACH_SERVER -> AuthException.FailedConnectingToServerException
                else -> AuthException.General(e.code())
            }
            throw throwable
        }
    }

    override suspend fun emailVerify(token: String) {
        try {
            val response = remoteService.emailVerify(token)
            userRepo.saveUser(response.user)
            tokenRepo.saveToken(response.jwt)
        } catch (e: HttpException) {
            val throwable = when (e.code()) {
                UNAUTHORIZED_HTTP_CODE -> AuthException.UserNotVerifiedException
                else -> AuthException.General(e.code())
            }
            throw throwable
        }
    }

    override fun isUserVerified(): Boolean {
        return tokenRepo.isTokenValid()
    }

    companion object {
        private const val CAN_NOT_REACH_SERVER = 503
        private const val UNAUTHORIZED_HTTP_CODE = 401
        private const val WRONG_CREDENTIAL_HTTP_CODE = 400
        private const val CONFLICT_HTTP_CODE = 409
    }
}