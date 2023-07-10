package com.gathering.android.auth.signup

sealed interface SignUpViewState {
    class NavigateToVerification(val email: String) : SignUpViewState

    class SignUpButtonVisibility(val isSignUpButtonEnabled: Boolean) : SignUpViewState
    sealed class Error(val errorMessage: String?) : SignUpViewState {
        class ShowGeneralError(errorMessage: String?) : Error(errorMessage)
        class ShowAuthenticationFailedError(errorMessage: String?) : Error(errorMessage)
        class ShowSuccessWithError(errorMessage: String?) : Error(errorMessage)
        class ShowInvalidEmailError(errorMessage: String?) : Error(errorMessage)
        class ShowInvalidPassError(errorMessage: String?) : Error(errorMessage)
        class ShowInvalidConfirmedPassError(errorMessage: String?) : Error(errorMessage)
        class ShowEmptyEmailError(errorMessage: String?) : Error(errorMessage)
        class ShowEmptyPassError(errorMessage: String?) : Error(errorMessage)
        class ShowEmptyConfirmedPassError(errorMessage: String?) : Error(errorMessage)
    }
}