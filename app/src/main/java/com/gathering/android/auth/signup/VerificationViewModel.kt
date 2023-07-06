package com.gathering.android.auth.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.AuthRepository
import com.gathering.android.auth.emailVerify.EmailVerifyRepository
import com.gathering.android.auth.model.ResponseState
import com.gathering.android.auth.sendVerificationEmail.ApiSendEmailVerificationRepository
import com.gathering.android.common.ResponseState

class VerificationViewModel @javax.inject.Inject constructor(
    private val repository: AuthRepository,
    private val newRepository: EmailVerifyRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<VerificationViewState>()
    val viewState: LiveData<VerificationViewState> by ::_viewState

    fun onSendEmailBtnClicked() {
        repository.sendEmailVerification() { result ->
            if (result is ResponseState.Success) {
                Log.d("verificationEmail", "email verification sent successfully.")
                _viewState.value = VerificationViewState.ButtonState(false)
                _viewState.value = VerificationViewState.StartTimer(seconds)
                { _viewState.value = VerificationViewState.ButtonState(true) }
            } else {
                _viewState.value =
                    VerificationViewState.Message("Failed to send Email Verification, try again!")
            }
        }
    }

    fun onVerifiedClicked() {
        if (repository.isUserVerified()) {
            _viewState.value = VerificationViewState.NavigateToHomeScreen
        } else {
            _viewState.value = VerificationViewState.Message("email must be verified")
        }

        newRepository.emailVerify("") {
            result ->
            if (result is ResponseState.Success<*>) {
                _viewState.value = VerificationViewState.NavigateToHomeScreen
            } else if (result is ResponseState.Failure){
               if(result.throwable is ApiSendEmailVerificationRepository.UserNotVerifiedException) {
                   // show user not verified
               } else {
                   // show exception message
               }
        }
    }

    fun onResume() {
        if (repository.isUserVerified()){
            _viewState.value = VerificationViewState.NavigateToHomeScreen
        }
    }

    companion object {
        private const val seconds: Int = 60
    }
}