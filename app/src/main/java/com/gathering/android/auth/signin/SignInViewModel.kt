package com.gathering.android.auth.signin

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.AuthRepository
import com.gathering.android.auth.model.ResponseState
import com.gathering.android.auth.model.SignInFailed
import com.gathering.android.auth.model.VerificationNeeded
import javax.inject.Inject


class SignInViewModel @Inject constructor(
    private val repository: AuthRepository
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
        repository.signInUser(email, pass, onResponseReady = { state ->
            when (state) {
                is ResponseState.Failure -> {
                    if (state.errorMessage is SignInFailed) {
                        _viewState.value =
                            SignInViewState.Error.ShowAuthenticationFailedError(state.errorMessage.message)
                    } else if (state.errorMessage is VerificationNeeded) {
                        _viewState.value = SignInViewState.NavigateToVerification
                    }
                }
                is ResponseState.Success -> _viewState.value = SignInViewState.NavigateToHome
            }
        })
    }

    fun onForgotPassTvClicked(){
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


