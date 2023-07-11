package com.gathering.android.auth.verification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.verification.repo.VerificationRepository
import com.gathering.android.common.ResponseState
import javax.inject.Inject

class VerificationViewModel @Inject constructor(
    private val verificationRepository: VerificationRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<VerificationViewState>()
    val viewState: LiveData<VerificationViewState> by ::_viewState

    fun onViewResumed(email: String?, token: String?) {
        if (token.isNullOrEmpty() && email.isNullOrEmpty()) return
        if (token.isNullOrEmpty()) {
            sendEmailVerification(email)
        } else {
            verifyEmail(token)
        }
    }

    fun sendEmailVerification(email: String?) {
        verificationRepository.sendEmailVerification(email ?: "") { result ->
            when (result) {
                is ResponseState.Failure -> {
                    _viewState.value = VerificationViewState.ButtonState(true)
                    _viewState.value =
                        VerificationViewState.ShowError("Failed to send Email Verification, try again!")
                }

                is ResponseState.Success<*> -> {
                    Log.d("verificationEmail", "email verification sent successfully.")
                    _viewState.value = VerificationViewState.ButtonState(true)
                }

                is ResponseState.SuccessWithError<*> -> {
                    VerificationViewState.ShowError("VerificationEMAIL SEND WITH ERROR, something went WRONG!")
                }
            }
        }
    }

    private fun verifyEmail(token: String?) {
        verificationRepository.emailVerify(token ?: "") { result ->
            when (result) {
                is ResponseState.Failure -> {
                    _viewState.value = VerificationViewState.ShowError("Failed to verify user")
                }

                is ResponseState.Success<*> -> {
                    Log.d("Email verify", "User verified successfully.")
                    _viewState.value = VerificationViewState.NavigateToHomeScreen
                }

                is ResponseState.SuccessWithError<*> -> {
                }
            }
        }
    }

    companion object {
        private const val seconds: Int = 60
    }

}