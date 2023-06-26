package com.gathering.android.auth.signup

sealed interface VerificationViewState {

    object NavigateToHomeScreen : VerificationViewState
    object NavigateToIntroPage : VerificationViewState
    object NavigateToVerification : VerificationViewState
    class Message(val text: String?) : VerificationViewState
    class ButtonState(val isEnabled: Boolean) : VerificationViewState
    class StartTimer(val seconds: Int, val onTimerFinished: () -> Unit) : VerificationViewState

}