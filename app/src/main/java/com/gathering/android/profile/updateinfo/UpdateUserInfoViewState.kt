package com.gathering.android.profile.updateinfo

import com.gathering.android.auth.model.User

sealed interface UpdateUserInfoViewState {

    class SaveChangesButtonVisibility(val isSaveChangesButtonEnabled: Boolean) :
        UpdateUserInfoViewState

    class ShowImage(val photo_url: String) : UpdateUserInfoViewState

    class ShowDisplayName(val displayName: String) : UpdateUserInfoViewState

    class ShowEmailAddress(val emailAddress: String) : UpdateUserInfoViewState

    object NavigateToAddPic : UpdateUserInfoViewState

    class NavigateToProfile(val user: User) : UpdateUserInfoViewState

    class ShowError(val errorMessage: String?) : UpdateUserInfoViewState
}