package com.gathering.android.auth.repo

sealed class AuthException : Throwable() {

    data object UserNotVerifiedException : AuthException()
    data object WrongCredentialsException : AuthException()
    data object EmailAlreadyInUseException : AuthException()

    class General(val code: Int) : AuthException()
}