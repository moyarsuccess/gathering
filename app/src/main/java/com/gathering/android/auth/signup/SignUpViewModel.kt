package com.gathering.android.auth.signup

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

class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val firebaseMessagingRepository: FirebaseRepository,
    private val validationChecker: ValidationChecker
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var signUpNavigator: SignUpNavigator? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is AuthException -> {
                when (throwable) {
                    AuthException.FailedConnectingToServerException -> CAN_NOT_REACH_THE_SERVER
                    AuthException.WrongCredentialsException -> SIGN_UP_FAILED
                    AuthException.EmailAlreadyInUseException -> EMAIL_ALREADY_IN_USE
                    else -> {
                        GENERAL_ERROR
                    }
                }
            }

            else -> {
                GENERAL_ERROR
            }
        }
        viewModelState.update { currentState ->
            currentState.copy(errorMessage = errorMessage)
        }
    }

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
                isInProgress = true
            )
        }
        if (!validationChecker.isEmailValid(email)) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(
                    errorMessage = INVALID_EMAIL_ADDRESS_FORMAT,
                    isInProgress = false
                )
            }
            return
        }

        if (!validationChecker.isPasswordValid(pass)) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(
                    errorMessage = INVALID_PASS_FORMAT,
                    isInProgress = false
                )
            }
            return
        }

        if (!validationChecker.isConfirmedPassValid(pass = pass, confirmedPass = confirmPass)) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(
                    errorMessage = INVALID_CONFIRMED_PASS,
                    isInProgress = false
                )
            }
            return
        }
        viewModelScope.launch(exceptionHandler) {
            val deviceToken = firebaseMessagingRepository.getDeviceToken()
            if (deviceToken.isNullOrEmpty()) {
                viewModelState.update { currentState ->
                    currentState.copy(
                        errorMessage = INVALID_DEVICE_TOKEN,
                        isInProgress = false
                    )
                }
                return@launch
            }
            repository.signUpUser(email = email, pass = pass, deviceToken = deviceToken)
            signUpNavigator?.navigateToVerification(email)
        }
        viewModelState.update { currentViewState ->
            currentViewState.copy(
                isInProgress = false
            )
        }
    }

    companion object {
        const val INVALID_CONFIRMED_PASS = "PASSWORDS DON'T MATCH"
        const val INVALID_PASS_FORMAT = "PLEASE ENTER A VALID PASSWORD"
        const val INVALID_EMAIL_ADDRESS_FORMAT = "PLEASE ENTER A VALID EMAIL ADDRESS"
        const val SIGN_UP_FAILED = "SIGN UP FAILED"
        const val CAN_NOT_REACH_THE_SERVER = "CAN NOT REACH THE SERVER"
        const val EMAIL_ALREADY_IN_USE = "EMAIL ADDRESS IS ALREADY IN USE!"
        const val INVALID_DEVICE_TOKEN = "INVALID_DEVICE_TOKEN"
        const val GENERAL_ERROR = "Ooops. something Wrong!"
    }
}