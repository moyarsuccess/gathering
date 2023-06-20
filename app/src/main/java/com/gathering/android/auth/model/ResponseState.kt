package com.gathering.android.auth.model

sealed interface ResponseState {

    class Success(val user: User) : ResponseState

    class Failure(val errorMessage: Exception) : ResponseState

}
    class VerificationNeeded(message: String) : Exception(message)
    class SignInFailed(message: String) : Exception(message)