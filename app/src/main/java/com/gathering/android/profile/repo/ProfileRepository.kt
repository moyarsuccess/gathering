package com.gathering.android.profile.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.common.UpdateProfileResponse

interface ProfileRepository {
    fun updateProfile(
        displayName: String,
        photoUri: String,
        onResponseReady: (ResponseState<UpdateProfileResponse>) -> Unit
    )
}