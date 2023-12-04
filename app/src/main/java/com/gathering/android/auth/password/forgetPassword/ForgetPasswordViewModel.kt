package com.gathering.android.auth.password.forgetPassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.auth.AuthException
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.common.ResponseState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ForgetPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private var forgetPasswordNavigator: ForgetPasswordNavigator? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is AuthException -> {
                when (throwable) {
                    AuthException.FailedConnectingToServerException -> FAILED_TO_SEND_RESET_PASSWORD_LINK
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
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = UiState()
    )

    data class UiState(
        val isInProgress: Boolean = false, var errorMessage: String? = null
    )

    fun onViewCreated(forgetPasswordNavigator: ForgetPasswordNavigator) {
        this.forgetPasswordNavigator = forgetPasswordNavigator
    }

    fun onSendLinkBtnClicked(email: String) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(isInProgress = true, errorMessage = null)
        }

        if (!isEmailValid(email)) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(isInProgress = false, errorMessage = INVALID_EMAIL_ADDRESS)
            }
            return
        }

        repository.forgetPassword(email) { state ->
            when (state) {
                is ResponseState.Failure -> {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(
                            errorMessage = FAILED_TO_SEND_RESET_PASSWORD_LINK, isInProgress = false
                        )
                    }
                }

                is ResponseState.Success -> {
                    Log.d("ResetEmail", RESET_PASS_EMAIL_SENT_SUCCESSFULLY)
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(isInProgress = false)
                    }
                    forgetPasswordNavigator?.navigateToResetPassInfoBottomSheet()
                }
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    companion object {
        private const val INVALID_EMAIL_ADDRESS = "INVALID EMAIL ADDRESS"
        private const val FAILED_TO_SEND_RESET_PASSWORD_LINK = "FAILED TO SEND RESET PASSWORD LINK"
        private const val RESET_PASS_EMAIL_SENT_SUCCESSFULLY =
            "RESET PASSWORD EMAIL WAS SEND SUCCESSFULLY"
        private const val GENERAL_ERROR = "Ooops. something Wrong!"
    }
}