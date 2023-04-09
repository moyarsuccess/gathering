package com.gathering.android.auth.signin


sealed interface SignInViewState {

    object NavigateToHome : SignInViewState
    class SignInButtonVisibility(val isSignInButtonEnabled: Boolean) : SignInViewState


    sealed class Error(val errorMessage: String?) : SignInViewState {
        class ShowGeneralError(errorMessage: String?) : Error(errorMessage)
        class ShowAuthenticationFailedError(errorMessage: String?) : Error(errorMessage)
        class ShowInvalidEmailError(errorMessage: String?) : Error(errorMessage)
        class ShowInvalidPassError(errorMessage: String?) : Error(errorMessage)
        class ShowEmptyEmailError(errorMessage: String?) : Error(errorMessage)
        class ShowEmptyPassError(errorMessage: String?) : Error(errorMessage)
    }
}

