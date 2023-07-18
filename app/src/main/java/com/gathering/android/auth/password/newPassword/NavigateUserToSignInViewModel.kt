package com.gathering.android.auth.password.newPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NavigateUserToSignInViewModel : ViewModel() {

    private val _viewState = MutableLiveData<NavigateUserToSignInViewState>()
    val viewState: LiveData<NavigateUserToSignInViewState> by ::_viewState

    fun onBackToSignInBtnClicked() {
        _viewState.value =
            NavigateUserToSignInViewState.NavigateToSignInPage
    }
}