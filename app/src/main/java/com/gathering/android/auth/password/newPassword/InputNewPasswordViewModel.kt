package com.gathering.android.auth.password.newPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InputNewPasswordViewModel : ViewModel() {

    private val _viewState = MutableLiveData<InputNewPasswordViewState>()
    val viewState: LiveData<InputNewPasswordViewState> by ::_viewState

    fun onSubmitBtnClicked(newPassword: String, confirmPassword: String) {
        if (newPassword == confirmPassword){
            // TODO extract token? see if token is matching user? wtf should i do !!!!
            _viewState.value=
                InputNewPasswordViewState.NavigateToSignInPage
        }else{
            InputNewPasswordViewState.Message("passwords don't match")
        }
    }

    fun onViewResumed(token: String?) {
        if (token.isNullOrEmpty()) {

        }
    }
}