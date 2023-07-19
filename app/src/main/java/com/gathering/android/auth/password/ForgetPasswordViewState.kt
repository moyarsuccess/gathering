package com.gathering.android.auth.password

sealed interface ForgetPasswordViewState {

    class Message(val text: String?) : ForgetPasswordViewState

    object NavigateToResetPassInfoBottomSheet : ForgetPasswordViewState
}