package com.gathering.android.profile.editProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.auth.model.User
import com.gathering.android.common.ResponseState
import com.gathering.android.common.UpdateProfileResponse
import com.gathering.android.common.UserRepo
import com.gathering.android.common.toImageUrl
import com.gathering.android.profile.repo.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class EditProfileViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private var isDisplayNameFilled: Boolean = false
    private var isImageUrlFilled: Boolean = false

    private var editProfileNavigator: EditProfileNavigator? = null


    private val viewModelState = MutableStateFlow(EditProfileViewModelState())
    val uiState: StateFlow<EditProfileUiState> = viewModelState.map { viewModelState ->
        EditProfileUiState(
            imageUri = viewModelState.imageUri,
            displayName = viewModelState.displayName,
            email = viewModelState.email,
            saveButtonEnable = viewModelState.saveButtonEnable
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = EditProfileUiState()
    )

    fun onViewCreated(editProfileNavigator: EditProfileNavigator) {
        this.editProfileNavigator = editProfileNavigator
        viewModelState.update { currentState ->
            val user = userRepo.getUser() ?: return
            currentState.copy(
                imageUri = user.photoName.toImageUrl(),
                displayName = user.displayName,
                email = user.email,
                saveButtonEnable = false
            )
        }
    }

    fun onImageButtonClicked() {
        editProfileNavigator?.navigateToAddPic()
    }

    fun onImageURLChanged(photoUri: String) {
        isImageUrlFilled = isImageUrlFilled(photoUri)
        val errorMessage = if (isImageUrlFilled) null else IMAGE_NOT_FILLED_MESSAGE
        viewModelState.update { currentState ->
            currentState.copy(imageUri = photoUri, errorMessage = errorMessage)
        }
        checkAllFieldsReady()
    }

    fun onDisplayNameChanged(displayName: String) {
        isDisplayNameFilled = isDisplayNameFilled(displayName)
        val errorMessage = if (isDisplayNameFilled) null else DISPLAY_NAME_NOT_FILLED_MESSAGE
        viewModelState.update { currentState ->
            currentState.copy(errorMessage = errorMessage)
        }
        checkAllFieldsReady()
    }

    private fun isDisplayNameFilled(imgUrl: String): Boolean {
        return imgUrl.isNotEmpty() && imgUrl.isNotBlank()
    }

    private fun isImageUrlFilled(displayName: String): Boolean {
        return displayName.isNotEmpty() && displayName.isNotBlank()
    }

    private fun checkAllFieldsReady() {
        viewModelState.update { currentState ->
            currentState.copy(saveButtonEnable = isAllFieldsFilled())
        }
    }

    private fun isAllFieldsFilled(): Boolean {
        return isDisplayNameFilled ||
                isImageUrlFilled
    }

    fun onSaveButtonClicked(displayName: String?, imageUrl: String?) {
        profileRepository.updateProfile(
            displayName = displayName,
            photoUri = imageUrl
        ) { responseState ->
            when (responseState) {
                is ResponseState.Failure -> {
                    viewModelState.update { currentState ->
                        currentState.copy(errorMessage = responseState.throwable.message)
                    }
                }

                is ResponseState.Success<UpdateProfileResponse> -> {
                    viewModelState.update { currentState ->
                        editProfileNavigator?.navigateToProfile(
                            User(
                                displayName = displayName ?: "",
                                photoName = imageUrl ?: ""
                            )
                        )
                        currentState.copy(displayName = displayName, imageUri = imageUrl)
                    }
                }

                else -> {}
            }
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
        private const val IMAGE_NOT_FILLED_MESSAGE = "Please pick or take a picture"
        private const val DISPLAY_NAME_NOT_FILLED_MESSAGE = "Please enter display name"
    }
}


