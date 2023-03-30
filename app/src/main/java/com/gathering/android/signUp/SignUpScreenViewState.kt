package com.gathering.android.signUp

import com.gathering.android.signUp.model.User

sealed interface SignUpScreenViewState {

    class NavigateToEventScreen(val user: User) : SignUpScreenViewState

    class SignUpButtonVisibility(val isSignUpButtonEnabled: Boolean) : SignUpScreenViewState

    sealed class Error(val errorMessage: String?) : SignUpScreenViewState {
        class ShowGeneralError(errorMessage: String?) : Error(errorMessage)
        class ShowAuthenticationFailedError(errorMessage: String?) : Error(errorMessage)
        class ShowInvalidEmailError(errorMessage: String?) : Error(errorMessage)
        class ShowInvalidPassError(errorMessage: String?) : Error(errorMessage)
        class ShowInvalidConfirmedPassError(errorMessage: String?) : Error(errorMessage)
        class ShowEmptyEmailError(errorMessage: String?) : Error(errorMessage)
        class ShowEmptyPassError(errorMessage: String?) : Error(errorMessage)
        class ShowEmptyConfirmedPassError(errorMessage: String?) : Error(errorMessage)
    }
}