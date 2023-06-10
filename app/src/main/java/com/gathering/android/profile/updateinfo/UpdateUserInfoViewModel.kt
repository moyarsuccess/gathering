package com.gathering.android.profile.updateinfo

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.model.User
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.RequestState
import com.gathering.android.profile.ProfileRepository
import javax.inject.Inject

class UpdateUserInfoViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) :
    ViewModel() {
    private var isDisplayNameFilled: Boolean = false
    private var isImageUrlFilled: Boolean = false

    private val _viewState = ActiveMutableLiveData<UpdateUserInfoViewState>()
    val viewState: ActiveMutableLiveData<UpdateUserInfoViewState> by ::_viewState

    fun onViewCreated() {
        val user = profileRepository.getUserData()
        _viewState.setValue(UpdateUserInfoViewState.ShowImage(user.photoUrl ?: ""))
        _viewState.setValue(UpdateUserInfoViewState.ShowDisplayName(user.displayName ?: ""))
        _viewState.setValue(UpdateUserInfoViewState.ShowEmailAddress(user.email ?: ""))
    }

    fun onImageButtonClicked() {
        _viewState.setValue(UpdateUserInfoViewState.NavigateToAddPic)
    }

    fun onSaveButtonClicked(bitmap: Bitmap?, user: User) {
        profileRepository.uploadPhoto(bitmap) { requestState ->
            when (requestState) {
                is RequestState.Failure -> viewState.setValue(UpdateUserInfoViewState.ShowError(""))
                is RequestState.Success<*> -> {
                    val photoUrl = requestState.data.toString()
                    updateProfile(user, photoUrl)
                }
            }
        }
    }

    private fun updateProfile(user: User, photoUrl: String) {
        profileRepository.updateDisplayNameAndPhotoURL(user, photoUrl) { requestState ->
            when (requestState) {
                is RequestState.Failure -> {
                    viewState.setValue(UpdateUserInfoViewState.ShowError(""))
                }

                is RequestState.Success<*> -> {
                    _viewState.setValue(
                        UpdateUserInfoViewState.NavigateToProfile(
                            UpdatedUserInfo(
                                updatedDisplayName = user.displayName ?: "",
                                updatedPhotoUrl = photoUrl
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

    fun onImageURLChanged(imgUrl: String) {
        isImageUrlFilled = isImageUrlFilled(imgUrl)
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