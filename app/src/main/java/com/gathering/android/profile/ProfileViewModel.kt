package com.gathering.android.profile

import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.TokenRepo
import com.gathering.android.common.UserRepo
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val tokenRepo: TokenRepo
) :
    ViewModel() {

    private val _viewState = ActiveMutableLiveData<ProfileViewState>()
    val viewState: ActiveMutableLiveData<ProfileViewState> by ::_viewState

    fun onViewCreated() {
        showMostRecentUserInfo()
    }

    fun onUserProfileUpdated() {
        showMostRecentUserInfo()
    }

    fun onFavoriteEventLayoutClicked() {
        _viewState.setValue(ProfileViewState.NavigateToFavoriteEvent)
    }

    fun onPersonalDataLayoutClicked() {
        _viewState.setValue(ProfileViewState.NavigateToPersonalData)
    }

    fun onSignUpButtonClicked() {
        userRepo.clearUser()
        tokenRepo.clearToken()
        _viewState.setValue(ProfileViewState.NavigateToIntro)
    }

    private fun showMostRecentUserInfo() {
        val user = userRepo.getUser() ?: return
        _viewState.setValue(ProfileViewState.SetEmail(user.email))
        _viewState.setValue(ProfileViewState.ShowImage(user.photoName))
        _viewState.setValue(ProfileViewState.SetDisplayName(user.displayName))
    }
}