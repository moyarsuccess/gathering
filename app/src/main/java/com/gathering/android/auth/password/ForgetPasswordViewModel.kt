package com.gathering.android.auth.password

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.password.repo.PasswordRepository
import com.gathering.android.common.ResponseState
import javax.inject.Inject

class ForgetPasswordViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<ForgetPasswordViewState>()
    val viewState: LiveData<ForgetPasswordViewState> by ::_viewState

    fun onSendLinkBtnClicked(email: String) {
        if (!isEmailValid(email)) {
            _viewState.value = ForgetPasswordViewState.Message("Invalid email address.")
            return
        }
        passwordRepository.forgetPassword(email) { state ->
            when (state) {
                is ResponseState.Failure -> {
                    _viewState.value =
                        ForgetPasswordViewState.Message("Failed to send reset password link.")
                }

                is ResponseState.Success<*> -> {
                    Log.d("ResetEmail", "reset password email was sent successfully.")
                    _viewState.value = ForgetPasswordViewState.NavigateToResetPassInfoBottomSheet
                }
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

}