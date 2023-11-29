package com.gathering.android.auth.password.newPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.notif.FirebaseRepository
import com.gathering.android.auth.password.repo.PasswordRepository
import com.gathering.android.common.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class InputNewPasswordViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository,
    private val firebaseMessagingRepository: FirebaseRepository
) : ViewModel() {

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

    private var inputNewPasswordNavigator: InputNewPasswordNavigator? = null

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
        viewModelScope.launch {
            val deviceToken = firebaseMessagingRepository.getDeviceToken()
            if (deviceToken.isNullOrEmpty()) {
                viewModelState.update { currentState ->
                    currentState.copy(
                        errorMessage = INVALID_DEVICE_TOKEN
                    )
                }
                return@launch
            }
            passwordRepository.resetPassword(
                token = token,
                password = newPassword,
                deviceToken = deviceToken ?: ""
            ) { response ->
                when (response) {
                    is ResponseState.Failure -> {
                        viewModelState.update { currentViewState ->
                            currentViewState.copy(
                                errorMessage = CAN_NOT_REACH_SERVER,
                                isInProgress = false
                            )
                        }
                    }

                    is ResponseState.Success -> {
                        viewModelState.update { currentViewState ->
                            currentViewState.copy(isInProgress = false)
                        }
                        inputNewPasswordNavigator?.navigateToHomeFragment()
                    }
                }
            }
        }
    }

    companion object {
        private const val LINK_NOT_VALID = "LINK NOT VALID"
        private const val PASSWORDS_DO_NOT_MATCH = "PASSWORDS DO NOT MATCH"
        private const val CAN_NOT_REACH_SERVER = "CAN NOT REACH SERVER"
        private const val INVALID_DEVICE_TOKEN = "INVALID_DEVICE_TOKEN"
    }
}


