package com.gathering.android.profile.repo

import com.gathering.android.common.ResponseState

interface ProfileRepository {
    fun updateProfile(
        displayName: String,
        photoUri: String,
        onResponseReady: (ResponseState) -> Unit
    )
}