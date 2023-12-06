package com.gathering.android.profile.repo

interface ProfileRepository {
    suspend fun updateProfile(
        displayName: String?,
        photoUri: String?
    )
}