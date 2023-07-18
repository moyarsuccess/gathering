package com.gathering.android.auth.password.newPassword

sealed interface InputNewPasswordViewState {

    class Message(val text: String?) : InputNewPasswordViewState

    object NavigateToSignInPage : InputNewPasswordViewState
}