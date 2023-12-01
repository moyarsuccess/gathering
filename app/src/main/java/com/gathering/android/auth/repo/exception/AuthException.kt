package com.gathering.android.auth.repo.exception

sealed class AuthException : Throwable() {

    data object UserNotVerifiedException : AuthException()
    data object WrongCredentialsException : AuthException()
    data object EmailAlreadyInUseException : AuthException()
}