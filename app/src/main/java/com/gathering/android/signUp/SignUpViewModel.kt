package com.gathering.android.signUp

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.signUp.model.AuthRepository
import com.gathering.android.signUp.model.ResponseState
import java.util.regex.Pattern
import javax.inject.Inject

class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<SignUpScreenViewState>()
    val viewState: LiveData<SignUpScreenViewState> by ::_viewState

    private var isEmailValid: Boolean = false
    private var isPassValid: Boolean = false
    private var isConfirmedPassValid: Boolean = false
    private val passWordPattern = Pattern.compile(PASSWORD_REGEX)

    fun onEmailAddressChanged(emailAddress: String) {
        isEmailValid = isEmailValid(emailAddress)
        val errorMessage = if (isEmailValid) null else INVALID_EMail_ADDRESS_FORMAT_ERROR_MESSAGE
        _viewState.value = SignUpScreenViewState.Error.ShowInvalidEmailError(errorMessage)
        checkAllFieldsReady()
    }

    fun onPasswordChanged(pass: String) {
        isPassValid = isPassValid(pass)
        val errorMessage = if (isPassValid) null else INVALID_PASS_FORMAT_ERROR_MESSAGE
        _viewState.value = SignUpScreenViewState.Error.ShowInvalidPassError(errorMessage)
        checkAllFieldsReady()
    }

    fun onConfirmedPasswordChanged(pass: String, confirmedPass: String) {
        isConfirmedPassValid = isConfirmedPassValid(pass, confirmedPass)
        val errorMessage =
            if (isConfirmedPassValid) null else INVALID_CONFIRMED_PASS_FORMAT_ERROR_MESSAGE
        _viewState.value = SignUpScreenViewState.Error.ShowInvalidConfirmedPassError(errorMessage)
        checkAllFieldsReady()
    }

    fun onSignUpButtonClicked(email: String, pass: String) {
        repository.signUpUser(email, pass, onResponseReady = { state ->
            when (state) {
                is ResponseState.Failure -> _viewState.value =
                    SignUpScreenViewState.Error.ShowAuthenticationFailedError(state.Error)
                is ResponseState.Success -> _viewState.value =
                    SignUpScreenViewState.NavigateToEventScreen(state.user)
            }
        })
    }

    private fun checkAllFieldsReady() {
        _viewState.value = SignUpScreenViewState.SignUpButtonVisibility(isAllFieldsValid())
    }

    private fun isEmailValid(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPassValid(pass: String): Boolean {
        val matcher = passWordPattern.matcher(pass)
        return matcher.matches()
    }

    private fun isConfirmedPassValid(pass: String, confirmedPass: String): Boolean {
        val matcher = passWordPattern.matcher(confirmedPass)
        return (pass == confirmedPass && matcher.matches())
    }

    private fun isAllFieldsValid(): Boolean {
        return isEmailValid &&
                isPassValid &&
                isConfirmedPassValid
    }

    companion object {
        private const val INVALID_EMail_ADDRESS_FORMAT_ERROR_MESSAGE =
            "Please enter a valid email address"
        private const val INVALID_PASS_FORMAT_ERROR_MESSAGE =
            "Please enter a valid password"
        private const val INVALID_CONFIRMED_PASS_FORMAT_ERROR_MESSAGE =
            "Please enter matched Password"

        private const val PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
    }
}