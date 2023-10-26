package com.gathering.android.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.TokenRepo
import com.gathering.android.common.UserRepo
import com.gathering.android.common.toImageUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val tokenRepo: TokenRepo
) : ViewModel() {

    private var profileNavigator: ProfileNavigator? = null

    private val viewModelState = MutableStateFlow(ProfileViewModelState())
    val uiState: StateFlow<ProfileUiState> = viewModelState.map { viewModelState ->
        ProfileUiState(
            imageUri = viewModelState.imageUri,
            displayName = viewModelState.displayName,
            email = viewModelState.email,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ProfileUiState()
    )

    fun onViewCreated(profileNavigator: ProfileNavigator) {
        this.profileNavigator = profileNavigator
        showMostRecentUserInfo()
    }

    fun onUserProfileUpdated() {
        showMostRecentUserInfo()
    }

    fun onFavoriteEventLayoutClicked() {
        profileNavigator?.navigateToFavoriteEvent()
    }

    fun onPersonalDataLayoutClicked() {
        profileNavigator?.navigateToEditProfile()
    }

    fun onSignOutButtonClicked() {
        userRepo.clearUser()
        tokenRepo.clearToken()
        profileNavigator?.navigateToIntro()
    }

    private fun showMostRecentUserInfo() {
        viewModelState.update { currentState ->
            val user = userRepo.getUser() ?: return
            currentState.copy(
                imageUri = user.photoName.toImageUrl(),
                displayName = user.displayName,
                email = user.email,
            )
        }
    }

    private data class ProfileViewModelState(
        val imageUri: String? = null,
        val displayName: String? = null,
        val email: String? = null,
    )
}


