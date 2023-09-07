package com.gathering.android.auth.signin

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.auth.signin.repo.SignInRepository
import com.gathering.android.common.ResponseState
import com.gathering.android.common.UserNotVerifiedException
import com.gathering.android.common.WrongCredentialsException
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val signInRepository: SignInRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = UiState()
    )

    private var signInNavigator: SignInNavigator? = null

    data class UiState(
        val isInProgress: Boolean = false,
        var errorMessage: String? = null,
    )

    fun onViewCreated(signInNavigator: SignInNavigator) {
        this.signInNavigator = signInNavigator
    }

    fun onSignInButtonClicked(email: String, pass: String) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(isInProgress = true, errorMessage = null)
        }
        if (!isEmailValid(email)) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(
                    errorMessage = INVALID_EMAIL_ADDRESS_FORMAT_ERROR_MESSAGE, isInProgress = false
                )
            }
            return
        }

        if (!isPassValid(pass)) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(
                    errorMessage = INVALID_PASS_FORMAT_ERROR_MESSAGE, isInProgress = false
                )
            }
            return
        }



        signInRepository.signInUser(email, pass, onResponseReady = { state ->
            when (state) {
                is ResponseState.Failure -> {
                    when (state.throwable) {
                        is WrongCredentialsException -> {
                            viewModelState.update { currentViewState ->
                                currentViewState.copy(
                                    errorMessage = SIGN_IN_FAILED,
                                    isInProgress = false,
                                )
                            }
                        }

                        is UserNotVerifiedException -> {
                            viewModelState.update { currentViewState ->
                                currentViewState.copy(
                                    errorMessage = EMAIL_NOT_VERIFIED, isInProgress = false
                                )
                            }
                            signInNavigator?.navigateToVerification()
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
                    signInNavigator?.navigateToHome()
                }
            }
        })
    }

    fun onForgotPassTvClicked() {
        signInNavigator?.navigateToPasswordReset()
    }

    private fun isEmailValid(email: String): Boolean {
        return !(TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun isPassValid(pass: String): Boolean {
        return !TextUtils.isEmpty(pass)
    }

    companion object {
        private const val INVALID_EMAIL_ADDRESS_FORMAT_ERROR_MESSAGE =
            "PLEASE ENTER A VALID EMAIL ADDRESS"
        private const val INVALID_PASS_FORMAT_ERROR_MESSAGE = "PLEASE ENTER A VALID PASSWORD"
        private const val SIGN_IN_FAILED = "SIGN IN FAILED"
        private const val EMAIL_NOT_VERIFIED = "EMAIL NOT VERIFIED"
        private const val CAN_NOT_REACH_THE_SERVER = "CAN NOT REACH THE SERVER"
    }
}