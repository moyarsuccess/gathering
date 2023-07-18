package com.gathering.android.auth.password.newPassword

sealed interface NavigateUserToSignInViewState {

    object NavigateToSignInPage : NavigateUserToSignInViewState
}