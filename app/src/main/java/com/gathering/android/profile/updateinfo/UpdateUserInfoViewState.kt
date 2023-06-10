package com.gathering.android.profile.updateinfo

import com.gathering.android.event.myevent.addevent.AddEventViewState

sealed interface UpdateUserInfoViewState {

    class SaveChangesButtonVisibility(val isSaveChangesButtonEnabled: Boolean) : UpdateUserInfoViewState

    class ShowImage(val imgUrl: String) : UpdateUserInfoViewState

    class ShowDisplayName(val displayName: String) : UpdateUserInfoViewState

    class ShowEmailAddress(val emailAddress: String) : UpdateUserInfoViewState

    object NavigateToAddPic : UpdateUserInfoViewState

    class NavigateToProfile(val updatedUserInfo: UpdatedUserInfo) : UpdateUserInfoViewState

    class ShowError(val errorMessage: String?) : UpdateUserInfoViewState
}