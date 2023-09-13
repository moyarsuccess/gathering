package com.gathering.android.profile.editProfile

data class EditProfileUiState(
    val imageUri: String? = null,
    val displayName: String? = null,
    val email: String? = null,
    val saveButtonEnable: Boolean? = false,
    val errorMessage: String? = null,
)