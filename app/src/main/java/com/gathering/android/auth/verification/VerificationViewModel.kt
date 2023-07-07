package com.gathering.android.auth.verification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.verification.repo.VerificationRepository
import com.gathering.android.common.ResponseState

class VerificationViewModel @javax.inject.Inject constructor(
    private val verificationRepository: VerificationRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<VerificationViewState>()
    val viewState: LiveData<VerificationViewState> by ::_viewState

    fun onSendEmailBtnClicked(email: String) {
        verificationRepository.sendEmailVerification(email) { result ->
            when (result) {
                is ResponseState.Failure -> {
                    _viewState.value =
                        VerificationViewState.Message("Failed to send Email Verification, try again!")
                }

                is ResponseState.Success<*> -> {
                    Log.d("verificationEmail", "email verification sent successfully.")
                    _viewState.value = VerificationViewState.ButtonState(false)
                    _viewState.value = VerificationViewState.StartTimer(seconds)
                    { _viewState.value = VerificationViewState.ButtonState(true) }
                }

                is ResponseState.SuccessWithError<*> -> {
                    // TODO Show proper error
                }
            }
        }
    }

    fun onVerificationLinkRecieved(token: String) {
        verificationRepository.emailVerify(token) { state ->
            when (state) {
                is ResponseState.Failure -> {
                    // TODO
                }

                is ResponseState.Success<*> -> {
                    // TODO
                }

                is ResponseState.SuccessWithError<*> -> {
                    // TODO
                }
            }
        }
    }

    fun onResume() {
        if (verificationRepository.isUserVerified()) {
            _viewState.value = VerificationViewState.NavigateToHomeScreen
        }
    }

    companion object {
        private const val seconds: Int = 60
    }

}