package com.gathering.android.profile.updateinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.model.User
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.ResponseState
import com.gathering.android.common.UpdateProfileResponse
import com.gathering.android.common.UserRepo
import com.gathering.android.profile.repo.ProfileRepository
import javax.inject.Inject

class UpdateUserInfoViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private var isDisplayNameFilled: Boolean = false
    private var isImageUrlFilled: Boolean = false
    private var photoUrl = ""

    private val _viewState = ActiveMutableLiveData<UpdateUserInfoViewState>()
    val viewState: LiveData<UpdateUserInfoViewState> by ::_viewState

    fun onViewCreated() {
        val user = userRepo.getUser() ?: return
        _viewState.setValue(UpdateUserInfoViewState.ShowImage(user.photoName))
        _viewState.setValue(UpdateUserInfoViewState.ShowDisplayName(user.displayName))
        _viewState.setValue(UpdateUserInfoViewState.ShowEmailAddress(user.email))
    }

    fun onImageButtonClicked() {
        _viewState.setValue(UpdateUserInfoViewState.NavigateToAddPic)
    }

    fun onSaveButtonClicked(displayName: String) {
        profileRepository.updateProfile(displayName, photoUrl) { responseState ->
            when (responseState) {
                is ResponseState.Failure -> _viewState.setValue(
                    UpdateUserInfoViewState.ShowError(
                        responseState.throwable.message
                    )
                )

                is ResponseState.Success<UpdateProfileResponse> -> {
                    _viewState.setValue(
                        UpdateUserInfoViewState.NavigateToProfile(
                            User(
                                displayName,
                                photoUrl
                            )
                        )
                    )
                }
            }
        }
    }

    fun onDisplayNameChanged(displayName: String) {
        isDisplayNameFilled = isDisplayNameFilled(displayName)
        val errorMessage = if (isDisplayNameFilled) null else DISPLAY_NAME_NOT_FILLED_MESSAGE
        _viewState.setValue(UpdateUserInfoViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onImageURLChanged(photo_Url: String) {
        isImageUrlFilled = isImageUrlFilled(photo_Url)
        photoUrl = photo_Url
        val errorMessage = if (isImageUrlFilled) null else IMAGE_NOT_FILLED_MESSAGE
        _viewState.setValue(UpdateUserInfoViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    private fun isDisplayNameFilled(imgUrl: String): Boolean {
        return imgUrl.isNotEmpty() && imgUrl.isNotBlank()
    }

    private fun isImageUrlFilled(displayName: String): Boolean {
        return displayName.isNotEmpty() && displayName.isNotBlank()
    }

    private fun checkAllFieldsReady() {
        _viewState.setValue(UpdateUserInfoViewState.SaveChangesButtonVisibility(isAllFieldsFilled()))
    }

    private fun isAllFieldsFilled(): Boolean {
        return isDisplayNameFilled ||
                isImageUrlFilled
    }

    companion object {
        private const val IMAGE_NOT_FILLED_MESSAGE = "Please pick or take a picture"
        private const val DISPLAY_NAME_NOT_FILLED_MESSAGE = "Please enter display name"
    }
}