package com.gathering.android.auth.verification

sealed interface VerificationViewState {

    object NavigateToHomeScreen : VerificationViewState

    class ShowError(val message: String?) : VerificationViewState
    class ButtonState(val isEnabled: Boolean) : VerificationViewState

}