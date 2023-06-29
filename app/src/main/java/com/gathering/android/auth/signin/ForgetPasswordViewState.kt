package com.gathering.android.auth.signin

sealed interface ForgetPasswordViewState {

    class Message(val text: String?) : ForgetPasswordViewState

    object NavigateToResetPassInfoBottomSheet: ForgetPasswordViewState
}