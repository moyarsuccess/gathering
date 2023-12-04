package com.gathering.android.auth

sealed class AuthException : Throwable() {
    data object UserNotVerifiedException : AuthException()
    data object WrongCredentialsException : AuthException()
    data object EmailAlreadyInUseException : AuthException()
}