package com.gathering.android.auth.signin

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.auth.AuthException
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.notif.FirebaseRepository
import com.gathering.android.utils.ValidationChecker
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val firebaseMessagingRepository: FirebaseRepository,
    private val validationChecker: ValidationChecker
) : ViewModel() {

    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = UiState()
    )

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var signInNavigator: SignInNavigator? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is AuthException -> {
                when (throwable) {
                    AuthException.FailedConnectingToServerException -> CAN_NOT_REACH_THE_SERVER
                    AuthException.UserNotVerifiedException -> EMAIL_NOT_VERIFIED
                    AuthException.WrongCredentialsException -> WRONG_CREDENTIALS
                    else -> GENERAL_ERROR
                }
            }

            else -> {
                GENERAL_ERROR
            }
        }
        viewModelState.update { currentState ->
            currentState.copy(errorMessage = errorMessage, isInProgress = false)
        }
    }

    data class UiState(
        val isInProgress: Boolean = false,
        var errorMessage: String? = null,
    )

    fun onViewCreated(signInNavigator: SignInNavigator) {
        this.signInNavigator = signInNavigator
    }

    fun onSignInButtonClicked(email: String, pass: String) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(isInProgress = true)
        }

        viewModelScope.launch(exceptionHandler) {
            val deviceToken = firebaseMessagingRepository.getDeviceToken()
            if (!validationChecker.isEmailValid(email)) {
                viewModelState.update { currentState ->
                    currentState.copy(
                        errorMessage = INVALID_EMAIL_ADDRESS_FORMAT_ERROR_MESSAGE,
                        isInProgress = false
                    )
                }
                return@launch
            }
            if (!validationChecker.isPasswordValid(pass)) {
                viewModelState.update { currentState ->
                    currentState.copy(
                        errorMessage = INVALID_PASS_FORMAT_ERROR_MESSAGE,
                        isInProgress = false
                    )
                }
                return@launch
            }
            if (deviceToken.isNullOrEmpty()) {
                viewModelState.update { currentState ->
                    currentState.copy(
                        errorMessage = INVALID_DEVICE_TOKEN,
                        isInProgress = false
                    )
                }
                return@launch
            }
            repository.signInUser(deviceToken = deviceToken, email = email, pass = pass)
            viewModelState.update { currentViewState ->
                currentViewState.copy(
                    isInProgress = false
                )
            }
            signInNavigator?.navigateToHome()
        }
    }

    fun onForgotPassTvClicked() {
        signInNavigator?.navigateToPasswordReset()
    }

    companion object {
        private const val INVALID_EMAIL_ADDRESS_FORMAT_ERROR_MESSAGE =
            "PLEASE ENTER A VALID EMAIL ADDRESS"
        private const val INVALID_PASS_FORMAT_ERROR_MESSAGE = "PLEASE ENTER A VALID PASSWORD"
        private const val WRONG_CREDENTIALS =
            "WE DO NOT RECOGNIZE THIS PASSWORD/EMAIL COMBINATION."
        private const val EMAIL_NOT_VERIFIED = "EMAIL NOT VERIFIED."
        private const val CAN_NOT_REACH_THE_SERVER = "CAN NOT REACH THE SERVER."
        private const val INVALID_DEVICE_TOKEN = "INVALID DEVICE TOKEN"
        private const val GENERAL_ERROR = "Ooops. something Wrong!"
    }
}