package com.gathering.android.profile

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.TokenRepository
import com.gathering.android.common.UserRepository
import com.gathering.android.common.toImageUrl
import com.gathering.android.profile.repo.ProfileException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var profileNavigator: ProfileNavigator? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is ProfileException -> {
                when (throwable) {
                    ProfileException.ServerNotRespondingException -> SERVER_NOT_RESPONDING_EXCEPTION
                    else -> GENERAL_ERROR
                }
            }

            else -> {
                GENERAL_ERROR
            }
        }
        viewModelState.update { currentState ->
            currentState.copy(errorMessage = errorMessage)
        }
    }

    private val viewModelState = MutableStateFlow(ProfileViewModelState())
    val uiState: StateFlow<ProfileUiState> = viewModelState.map { viewModelState ->
        ProfileUiState(
            imageUri = viewModelState.imageUri,
            displayName = viewModelState.displayName,
            email = viewModelState.email,
            errorMessage = viewModelState.errorMessage
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
        userRepository.clearUser()
        tokenRepository.clearToken()
        profileNavigator?.navigateToIntro()
    }

    private fun showMostRecentUserInfo() {
        viewModelScope.launch(exceptionHandler) {
            val user = userRepository.getUser() ?: return@launch
            viewModelState.update { currentState ->
                currentState.copy(
                    imageUri = user.photoName.toImageUrl(),
                    displayName = user.displayName,
                    email = user.email,
                )
            }
        }
    }

    private data class ProfileViewModelState(
        val imageUri: String? = null,
        val displayName: String? = null,
        val email: String? = null,
        val errorMessage: String? = null,
    )

    companion object {
        private const val GENERAL_ERROR = "Ooops. something Wrong!"
        private const val SERVER_NOT_RESPONDING_EXCEPTION = "Server not found"
    }
}


