package com.gathering.android.profile.repo

import com.gathering.android.common.UpdateProfileResponse

interface ProfileRepository {
    suspend fun updateProfile(
        displayName: String?,
        photoUri: String?
    ): UpdateProfileResponse?
}