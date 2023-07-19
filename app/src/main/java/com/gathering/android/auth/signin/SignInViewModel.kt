package com.gathering.android.auth.signin

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.signin.repo.SignInRepository
import com.gathering.android.common.AuthorizedResponse
import com.gathering.android.common.ResponseState
import com.gathering.android.common.UserNotVerifiedException
import com.gathering.android.common.WrongCredentialsException
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val signInRepository: SignInRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<SignInViewState>()
    val viewState: LiveData<SignInViewState> by ::_viewState

    private var isEmailValid: Boolean = false
    private var isPassValid: Boolean = false

    fun onEmailAddressChanged(emailAddress: String) {
        isEmailValid = isEmailValid(emailAddress)
        val errorMessage = if (isEmailValid) null else INVALID_EMAIL_ADDRESS_FORMAT_ERROR_MESSAGE
        _viewState.value = SignInViewState.Error.ShowInvalidEmailError(errorMessage)
        checkAllFieldsReady()
    }

    fun onPasswordChanged(pass: String) {
        isPassValid = isPassValid(pass)
        val errorMessage = if (isPassValid) null else INVALID_PASS_FORMAT_ERROR_MESSAGE
        _viewState.value = SignInViewState.Error.ShowInvalidPassError(errorMessage)
        checkAllFieldsReady()
    }

    fun onSignInButtonClicked(email: String, pass: String) {
        signInRepository.signInUser(email, pass, onResponseReady = { state ->
            when (state) {
                is ResponseState.Failure -> {
                    when (state.throwable) {
                        is WrongCredentialsException -> {
                            _viewState.value =
                                SignInViewState.Error.ShowAuthenticationFailedError("Sign in failed")
                        }

                        is UserNotVerifiedException -> {
                            _viewState.value =
                                SignInViewState.Error.ShowUserNotVerifiedError("email not verified")
                            _viewState.value =
                                SignInViewState.NavigateToVerification
                        }

                        else -> {
                            _viewState.value =
                                SignInViewState.Error.ShowGeneralError("can not reach the server")
                        }
                    }
                }

                is ResponseState.Success<AuthorizedResponse> -> {
                    _viewState.value =
                        SignInViewState.NavigateToHome
                }
            }
        })
    }

    fun onForgotPassTvClicked() {
        _viewState.value = SignInViewState.NavigateToPasswordReset
    }

    private fun checkAllFieldsReady() {
        _viewState.value = SignInViewState.SignInButtonVisibility(isAllFieldsValid())
    }

    private fun isEmailValid(email: String): Boolean {
        return !(TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun isPassValid(pass: String): Boolean {
        return !TextUtils.isEmpty(pass)
    }

    private fun isAllFieldsValid(): Boolean {
        return isEmailValid && isPassValid
    }

    companion object {
        private const val INVALID_EMAIL_ADDRESS_FORMAT_ERROR_MESSAGE =
            "Please enter a valid email address"
        private const val INVALID_PASS_FORMAT_ERROR_MESSAGE = "Please enter a valid password"
    }
}


