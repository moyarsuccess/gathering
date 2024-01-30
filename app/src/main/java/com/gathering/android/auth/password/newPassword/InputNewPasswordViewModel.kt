package com.gathering.android.auth.password.newPassword

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.auth.AuthException
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.notif.FirebaseRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class InputNewPasswordViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val firebaseMessagingRepository: FirebaseRepository
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var inputNewPasswordNavigator: InputNewPasswordNavigator? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is AuthException -> {
                when (throwable) {
                    AuthException.FailedConnectingToServerException -> CAN_NOT_REACH_SERVER
                    else -> GENERAL_ERROR
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
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UiState()
    )

    data class UiState(
        val isInProgress: Boolean = false,
        var errorMessage: String? = null
    )

    fun onViewCreated(inputNewPasswordNavigator: InputNewPasswordNavigator) {
        this.inputNewPasswordNavigator = inputNewPasswordNavigator
    }

    fun onSubmitBtnClicked(token: String?, newPassword: String, confirmPassword: String) {
        if (token.isNullOrBlank()) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(errorMessage = LINK_NOT_VALID)
            }
            return
        }
        if (newPassword != confirmPassword) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(errorMessage = PASSWORDS_DO_NOT_MATCH)
            }
            return
        }
        viewModelState.update { currentViewState ->
            currentViewState.copy(isInProgress = true, errorMessage = null)
        }
        viewModelScope.launch(exceptionHandler) {
            val deviceToken = firebaseMessagingRepository.getDeviceToken()
            if (deviceToken.isNullOrEmpty()) {
                viewModelState.update { currentState ->
                    currentState.copy(
                        errorMessage = INVALID_DEVICE_TOKEN
                    )
                }
                return@launch
            }
                repository.resetPassword(
                    password = newPassword,
                    token = token,
                    deviceToken = deviceToken
                )
                viewModelState.update { currentViewState ->
                    currentViewState.copy(isInProgress = false)
                }
                inputNewPasswordNavigator?.navigateToIntroFragment()
        }
    }

    companion object {
        const val LINK_NOT_VALID = "LINK IS NOT VALID"
        const val PASSWORDS_DO_NOT_MATCH = "PASSWORDS DO NOT MATCH"
        const val CAN_NOT_REACH_SERVER = "CAN NOT REACH SERVER"
        const val INVALID_DEVICE_TOKEN = "INVALID DEVICE TOKEN"
        const val GENERAL_ERROR = "Ooops. something Wrong!"
    }
}