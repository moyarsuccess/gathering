package com.gathering.android.auth.signup

sealed interface VerificationViewState {

    object NavigateToHomeScreen : VerificationViewState

    object NavigateToIntroPage : VerificationViewState

    object NavigateToVerification : VerificationViewState

    object NavigateToSignIn : VerificationViewState

    class Message(val text: String?) : VerificationViewState

    class SendEmailAgainVisibility(val isSendEmailAgainVisibility: Boolean) : VerificationViewState

}