package com.gathering.android.auth.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.common.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class VerificationViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UiState()
    )

    data class UiState(
        val isInProgress: Boolean = false, var message: String? = null
    )

    private var verificationNavigator: VerificationNavigator? = null


    fun onViewCreated(verificationNavigator: VerificationNavigator) {
        this.verificationNavigator = verificationNavigator
    }

    fun onViewResumed(email: String?, token: String?) {
        if (token.isNullOrEmpty() && email.isNullOrEmpty()) return
        if (token.isNullOrEmpty()) {
            onSendEmailVerificationClicked(email)
        } else {
            verifyEmail(token)
        }
    }

    fun onSendEmailVerificationClicked(email: String?) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(
                isInProgress = true,
            )
        }
        repository.sendEmailVerification(email ?: "") { state ->
            when (state) {
                is ResponseState.Failure -> {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(
                            isInProgress = false, message = FAILED_TO_SEND_EMAIL_VERIFICATION
                        )
                    }
                }

                is ResponseState.Success -> {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(
                            isInProgress = false, message = VERIFICATION_EMAIL_SENT_SUCCESSFULLY
                        )
                    }
                }
            }
        }
    }

    private fun verifyEmail(token: String?) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(
                isInProgress = true,
            )
        }
        repository.emailVerify(token ?: "") { state ->
            when (state) {
                is ResponseState.Failure -> {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(isInProgress = false, message = FAILED_TO_VERIFY_USER)
                    }
                }

                is ResponseState.Success -> {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(
                            isInProgress = false, message = USER_VERIFIED_SUCCESSFULLY
                        )
                    }
                    verificationNavigator?.navigateToHomeScreen()
                }
            }
        }
    }

    companion object {
        private const val VERIFICATION_EMAIL_SENT_SUCCESSFULLY =
            "EMAIL VERIFICATION SENT SUCCESSFULLY"
        private const val FAILED_TO_SEND_EMAIL_VERIFICATION =
            "FAILED TO SEND EMAIL VERIFICATION, TRY AGAIN!"
        private const val FAILED_TO_VERIFY_USER = "FAILED TO VERIFY THE USER"
        private const val USER_VERIFIED_SUCCESSFULLY = "USER VERIFIED SUCCESSFULLY"
    }
}