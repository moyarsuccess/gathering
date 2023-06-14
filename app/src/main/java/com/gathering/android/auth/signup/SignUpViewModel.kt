package com.gathering.android.auth.signup

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.AuthRepository
import com.gathering.android.auth.model.ResponseState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern
import javax.inject.Inject

class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<SignUpViewState>()
    val viewState: LiveData<SignUpViewState> by ::_viewState

    private var isEmailValid: Boolean = false
    private var isPassValid: Boolean = false
    private var isConfirmedPassValid: Boolean = false
    private val passWordPattern = Pattern.compile(PASSWORD_REGEX)

    fun onEmailAddressChanged(emailAddress: String) {
        isEmailValid = isEmailValid(emailAddress)
        val errorMessage = if (isEmailValid) null else INVALID_EMail_ADDRESS_FORMAT_ERROR_MESSAGE
        _viewState.value = SignUpViewState.Error.ShowInvalidEmailError(errorMessage)
        checkAllFieldsReady()
    }

    fun onPasswordChanged(pass: String) {
        isPassValid = isPassValid(pass)
        val errorMessage = if (isPassValid) null else INVALID_PASS_FORMAT_ERROR_MESSAGE
        _viewState.value = SignUpViewState.Error.ShowInvalidPassError(errorMessage)
        checkAllFieldsReady()
    }

    fun onConfirmedPasswordChanged(pass: String, confirmedPass: String) {
        isConfirmedPassValid = isConfirmedPassValid(pass, confirmedPass)
        val errorMessage =
            if (isConfirmedPassValid) null else INVALID_CONFIRMED_PASS_FORMAT_ERROR_MESSAGE
        _viewState.value = SignUpViewState.Error.ShowInvalidConfirmedPassError(errorMessage)
        checkAllFieldsReady()
    }

    fun onSignUpButtonClicked(email: String, pass: String) {
        repository.signUpUser(email, pass) { state ->
            when (state) {
                is ResponseState.Failure -> _viewState.value =
                    SignUpViewState.Error.ShowAuthenticationFailedError("error")
                is ResponseState.Success -> {
                    sendEmailVerification()
                    _viewState.value = SignUpViewState.NavigateToHomeScreen
                }
            }
        }
    }
    private fun checkAllFieldsReady() {
        _viewState.value = SignUpViewState.SignUpButtonVisibility(isAllFieldsValid())
    }

    private fun isEmailValid(email: String): Boolean {
        return !(TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
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
        return isEmailValid && isPassValid && isConfirmedPassValid
    }

    private fun sendEmailVerification() {
        val user = Firebase.auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _viewState.value = SignUpViewState.Message("Email verification sent. Please check your email")
            } else {
                _viewState.value = SignUpViewState.Message("Failed to send Email Verification, try again!")
            }
        }
    }

    companion object {
        private const val INVALID_EMail_ADDRESS_FORMAT_ERROR_MESSAGE =
            "Please enter a valid email address"
        private const val INVALID_PASS_FORMAT_ERROR_MESSAGE = "Please enter a valid password"
        private const val INVALID_CONFIRMED_PASS_FORMAT_ERROR_MESSAGE =
            "Please enter matched Password"

        private const val PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
    }
}