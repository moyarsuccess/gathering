package com.gathering.android.profile.updateinfo

import java.io.Serializable

data class UpdatedUserInfo(
    val updatedDisplayName: String,
    val updatedPhotoUrl: String,
    val email: String = ""
) : Serializable