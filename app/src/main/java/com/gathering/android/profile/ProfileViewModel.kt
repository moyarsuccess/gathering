package com.gathering.android.profile

import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) :
    ViewModel() {

    private val _viewState = ActiveMutableLiveData<ProfileViewState>()
    val viewState: ActiveMutableLiveData<ProfileViewState> by ::_viewState

    fun onViewCreated() {
        val user = profileRepository.getUserData()
        _viewState.setValue(ProfileViewState.SetEmail(user.email.toString()))
        _viewState.setValue(ProfileViewState.ShowImage(user.photoUrl.toString()))
        _viewState.setValue(ProfileViewState.SetDisplayName(user.displayName.toString()))
    }

    fun onDisplayNameChanged(displayName: String) {
        _viewState.setValue(ProfileViewState.SetDisplayName(displayName))
    }

    fun onImageChanged(imgUrl: String) {
        _viewState.setValue(ProfileViewState.ShowImage(imgUrl))
    }

    fun onFavoriteEventLayoutClicked() {
        _viewState.setValue(ProfileViewState.NavigateToFavoriteEvent)
    }

    fun onPersonalDataLayoutClicked() {
        _viewState.setValue(ProfileViewState.NavigateToPersonalData)
    }

    fun onSignUpButtonClicked() {
        
    }
}