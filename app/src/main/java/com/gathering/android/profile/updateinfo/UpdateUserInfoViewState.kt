package com.gathering.android.profile.updateinfo

sealed interface UpdateUserInfoViewState {

    class SaveChangesButtonVisibility(val isSaveChangesButtonEnabled: Boolean) :
        UpdateUserInfoViewState

    class ShowImage(val imgUrl: String) : UpdateUserInfoViewState

    class ShowDisplayName(val displayName: String) : UpdateUserInfoViewState

    class ShowEmailAddress(val emailAddress: String) : UpdateUserInfoViewState

    object NavigateToAddPic : UpdateUserInfoViewState

    object NavigateToProfile : UpdateUserInfoViewState

    class ShowError(val errorMessage: String?) : UpdateUserInfoViewState
}