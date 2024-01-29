package com.gathering.android.auth.repo

import com.gathering.android.auth.AuthException
import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.TokenRepository
import com.gathering.android.common.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class ApiAuthRepository @Inject constructor(
    private val remoteService: AuthRemoteService,
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
) : AuthRepository {
    override suspend fun forgetPassword(email: String): GeneralApiResponse =
        withContext(Dispatchers.IO) {
            remoteService.forgetPassword(email = email)
        }

    override suspend fun resetPassword(
        token: String, password: String, deviceToken: String
    ): AuthorizedResponse = withContext(Dispatchers.IO) {
        remoteService.resetPassword(
            password = password, deviceToken = deviceToken, token = token
        )
    }

    override suspend fun signInUser(
        email: String, pass: String, deviceToken: String
    ): AuthorizedResponse = withContext(Dispatchers.IO) {
        try {
            val response =
                remoteService.signIn(password = pass, deviceToken = deviceToken, email = email)
            tokenRepository.saveToken(response.jwt)
            userRepository.saveUser(response.user)
            response
        } catch (e: HttpException) {
            val throwable = when (e.code()) {
                UNAUTHORIZED_HTTP_CODE -> AuthException.UserNotVerifiedException
                WRONG_CREDENTIAL_HTTP_CODE -> AuthException.WrongCredentialsException
                else -> AuthException.General(e.code())
            }
            throw throwable
        }
    }

    override suspend fun signUpUser(
        email: String,
        pass: String,
        deviceToken: String
    ): GeneralApiResponse =
        withContext(Dispatchers.IO) {
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

    override suspend fun sendEmailVerification(
        email: String
    ): GeneralApiResponse = withContext(Dispatchers.IO) {
        try {
            remoteService.sendEmailVerification(email = email)
        } catch (e: HttpException) {
            val throwable = when (e.code()) {
                CAN_NOT_REACH_SERVER -> AuthException.FailedConnectingToServerException
                else -> AuthException.General(e.code())
            }
            throw throwable
        }
    }

    override suspend fun emailVerify(
        token: String
    ): AuthorizedResponse = withContext(Dispatchers.IO) {
        try {
            remoteService.emailVerify(token = token)
        } catch (e: HttpException) {
            val throwable = when (e.code()) {
                UNAUTHORIZED_HTTP_CODE -> AuthException.UserNotVerifiedException
                else -> AuthException.General(e.code())
            }
            throw throwable
        }
    }

    override fun isUserVerified(): Boolean {
        return tokenRepository.isTokenValid()
    }

    companion object {
        private const val CAN_NOT_REACH_SERVER = 503
        private const val UNAUTHORIZED_HTTP_CODE = 401
        private const val WRONG_CREDENTIAL_HTTP_CODE = 400
        private const val CONFLICT_HTTP_CODE = 409
    }
}