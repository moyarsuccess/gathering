package com.gathering.android.auth.signin

interface SignInNavigator {

    fun navigateToHome()

    fun navigateToVerification(email: String)

    fun navigateToPasswordReset()
}