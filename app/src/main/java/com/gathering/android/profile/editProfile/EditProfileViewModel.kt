package com.gathering.android.profile.editProfile

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.auth.model.User
import com.gathering.android.common.UserRepository
import com.gathering.android.common.toImageUrl
import com.gathering.android.event.FILE_NOT_FOUND_EXCEPTION
import com.gathering.android.event.GENERAL_ERROR
import com.gathering.android.event.UPDATE_PROFILE_REQUEST_FAILED
import com.gathering.android.profile.repo.ProfileException
import com.gathering.android.profile.repo.ProfileRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var editProfileNavigator: EditProfileNavigator? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is ProfileException -> {
                when (throwable) {
                    ProfileException.ServerNotRespondingException -> UPDATE_PROFILE_REQUEST_FAILED
                    is ProfileException.GeneralException -> GENERAL_ERROR
                    ProfileException.FileNotFoundException -> FILE_NOT_FOUND_EXCEPTION
                }
            }

            else -> {
                GENERAL_ERROR
            }
        }
        viewModelState.update { currentState ->
            currentState.copy(
                errorMessage = errorMessage,
            )
        }
    }

    private val viewModelState = MutableStateFlow(EditProfileViewModelState())
    val uiState: StateFlow<EditProfileUiState> = viewModelState.map { viewModelState ->
        EditProfileUiState(
            imageUri = viewModelState.imageUri,
            displayName = viewModelState.displayName,
            email = viewModelState.email,
            errorMessage = viewModelState.errorMessage,
            saveButtonEnable = viewModelState.saveButtonEnable
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = EditProfileUiState()
    )

    fun onViewCreated(editProfileNavigator: EditProfileNavigator) {
        this.editProfileNavigator = editProfileNavigator

        viewModelScope.launch(exceptionHandler) {
            val user = userRepository.getUser() ?: return@launch
            viewModelState.update { currentState ->
                currentState.copy(
                    imageUri = user.photoName.toImageUrl(),
                    displayName = user.displayName,
                    email = user.email,
                    saveButtonEnable = false
                )
            }
        }
    }

    fun onImageButtonClicked() {
        editProfileNavigator?.navigateToAddPic()
    }

    fun onImageURLChanged(photoUri: String) {
        viewModelScope.launch(exceptionHandler) {
            if (photoUri.isEmpty()) {
                viewModelState.update { currentState ->
                    currentState.copy(errorMessage = IMAGE_NOT_FILLED_MESSAGE)
                }
                return@launch
            }
            viewModelState.update { currentState ->
                currentState.copy(imageUri = photoUri, errorMessage = null)
            }
            checkAllFieldsReady()
        }
    }

    fun onDisplayNameChanged(displayName: String) {
        viewModelScope.launch(exceptionHandler) {
            if (displayName.isEmpty()) {
                viewModelState.update { currentState ->
                    currentState.copy(errorMessage = DISPLAY_NAME_NOT_FILLED_MESSAGE)
                }
                return@launch
            }
            viewModelState.update { currentState ->
                currentState.copy(displayName = displayName, errorMessage = null)
            }
            checkAllFieldsReady()
        }
    }

    private fun checkAllFieldsReady() {
        viewModelState.update { currentState ->
            currentState.copy(saveButtonEnable = isAllFieldsFilled())
        }
    }

    private fun isAllFieldsFilled(): Boolean {
        return !viewModelState.value.displayName.isNullOrEmpty() ||
                !viewModelState.value.imageUri.isNullOrEmpty()
    }

    fun onSaveButtonClicked(displayName: String?, imageUrl: String?) {
        viewModelScope.launch(exceptionHandler) {
            if (displayName.isNullOrEmpty() && imageUrl.isNullOrEmpty()) return@launch

            profileRepository.updateProfile(
                displayName = displayName, photoUri = imageUrl
            ) ?: return@launch

            viewModelState.update { currentState ->
                currentState.copy(displayName = displayName, imageUri = imageUrl)
            }

            editProfileNavigator?.navigateToProfile(
                User(
                    displayName = displayName ?: "",
                    photoName = imageUrl ?: ""
                )
            )
        }
    }

    private data class EditProfileViewModelState(
        val imageUri: String? = null,
        val displayName: String? = null,
        val email: String? = null,
        val errorMessage: String? = null,
        val saveButtonEnable: Boolean? = false,
    )

    companion object {
        const val IMAGE_NOT_FILLED_MESSAGE = "Please pick or take a picture"
        const val DISPLAY_NAME_NOT_FILLED_MESSAGE = "Please enter display name"
    }
}


