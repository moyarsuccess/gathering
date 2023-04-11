package com.gathering.android.navhost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.AuthRepository
import javax.inject.Inject

class NavHostViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<NavHostViewState>()
    val viewState: LiveData<NavHostViewState> by ::_viewState

    fun onScreenResumed() {
        if (!authRepository.isSignedIn()) {
            _viewState.value = NavHostViewState.NavigateToIntroScreen
        }
    }
}