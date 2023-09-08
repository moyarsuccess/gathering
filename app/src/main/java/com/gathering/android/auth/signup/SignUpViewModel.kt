package com.gathering.android.auth.signup

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.auth.signup.repo.SignUpRepository
import com.gathering.android.common.EmailAlreadyInUse
import com.gathering.android.common.ResponseState
import com.gathering.android.common.WrongCredentialsException
import kotlinx.coroutines.flow.*
import java.util.regex.Pattern
import javax.inject.Inject

class SignUpViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository
) : ViewModel() {

    private var signUpNavigator: SignUpNavigator? = null

    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = UiState()
    )

    data class UiState(
        val isInProgress: Boolean = false,
        var errorMessage: String? = null,
    )

    fun onViewCreated(signUpNavigator: SignUpNavigator) {
        this.signUpNavigator = signUpNavigator
    }

    fun onSignUpButtonClicked(email: String, pass: String, confirmPass: String) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(
                isInProgress = true, errorMessage = null
            )
        }
        if (!isEmailValid(email)) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(
                    errorMessage = INVALID_EMAIL_ADDRESS_FORMAT, isInProgress = false
                )
            }
            return
        }

        if (!isPassValid(pass)) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(
                    errorMessage = INVALID_PASS_FORMAT, isInProgress = false
                )
            }
            return
        }

        if (!isConfirmedPassValid(pass, confirmPass)) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(
                    errorMessage = INVALID_CONFIRMED_PASS, isInProgress = false
                )
            }
            return
        }
        signUpRepository.signUpUser(email, pass) { state ->
            when (state) {
                is ResponseState.Failure -> {
                    when (state.throwable) {
                        is WrongCredentialsException -> {
                            viewModelState.update { currentViewState ->
                                currentViewState.copy(
                                    errorMessage = SIGN_UP_FAILED,
                                    isInProgress = false,
                                )
                            }
                        }
                        is EmailAlreadyInUse -> {
                            viewModelState.update { currentViewState ->
                                currentViewState.copy(
                                    errorMessage = EMAIL_ALREADY_IN_USE,
                                    isInProgress = false,
                                )
                            }
                        }
                        else -> {
                            viewModelState.update { currentViewState ->
                                currentViewState.copy(
                                    errorMessage = CAN_NOT_REACH_THE_SERVER,
                                    isInProgress = false,
                                )
                            }
                        }
                    }
                }
                is ResponseState.Success -> {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(
                            isInProgress = true
                        )
                    }
                    signUpNavigator?.navigateToVerification()
                }
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return !(TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun isPassValid(pass: String): Boolean {
        val matcher = Pattern.compile(PASSWORD_REGEX).matcher(pass)
        return matcher.matches()
    }

    private fun isConfirmedPassValid(pass: String, confirmedPass: String): Boolean {
        val matcher = Pattern.compile(PASSWORD_REGEX).matcher(confirmedPass)
        return (pass == confirmedPass && matcher.matches())
    }
    companion object {
        private const val INVALID_EMAIL_ADDRESS_FORMAT = "PLEASE ENTER A VALID EMAIL ADDRESS"
        private const val INVALID_PASS_FORMAT = "PLEASE ENTER A VALID PASSWORD"
        private const val INVALID_CONFIRMED_PASS = "PASSWORDS DON'T MATCH"
        private const val PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        private const val SIGN_UP_FAILED = "SIGN UP FAILED"
        private const val CAN_NOT_REACH_THE_SERVER = "CAN NOT REACH THE SERVER"
        private const val EMAIL_ALREADY_IN_USE = "EMAIL ALREADY IS USE"
    }
}