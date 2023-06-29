package com.gathering.android.auth.signin

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.AuthRepository
import com.gathering.android.auth.model.ResponseState
import javax.inject.Inject

class ForgetPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<ForgetPasswordViewState>()
    val viewState: LiveData<ForgetPasswordViewState> by ::_viewState

    fun onSendLinkBtnClicked(email: String) {
        if (!isEmailValid(email)) {
            _viewState.value = ForgetPasswordViewState.Message("Invalid email address.")
            return
        }
        repository.resetPassword(email) { state ->
            when (state) {
                is ResponseState.Success -> {
                    Log.d("ResetEmail", "reset password email was sent successfully.")
                    _viewState.value = ForgetPasswordViewState.NavigateToResetPassInfoBottomSheet
                }
                is ResponseState.Failure -> {
                    _viewState.value =
                        ForgetPasswordViewState.Message("Failed to send reset password link.")
                }
            }
        }
    }
    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

}