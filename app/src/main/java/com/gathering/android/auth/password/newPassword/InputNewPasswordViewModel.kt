package com.gathering.android.auth.password.newPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.password.repo.PasswordRepository
import com.gathering.android.common.ResponseState
import javax.inject.Inject

class InputNewPasswordViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<InputNewPasswordViewState>()
    val viewState: LiveData<InputNewPasswordViewState> by ::_viewState

    fun onSubmitBtnClicked(token: String?, newPassword: String, confirmPassword: String) {
        if (token.isNullOrBlank()) {
            _viewState.value = InputNewPasswordViewState.Message("link not valid.")
            return
        }
        if (newPassword != confirmPassword) {
            _viewState.value = InputNewPasswordViewState.Message("passwords don't match.")
            return
        }
        passwordRepository.resetPassword(token, newPassword) { response ->
            when (response) {
                is ResponseState.Success -> _viewState.value =
                    InputNewPasswordViewState.NavigateToHomeFragment
                is ResponseState.Failure -> {
                    _viewState.value = InputNewPasswordViewState.Message(response.throwable.message)
                }
            }
        }
    }
}