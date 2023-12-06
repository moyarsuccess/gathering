package com.gathering.android.profile.repo

import android.content.Context
import com.gathering.android.common.LOCAL_CONTENT_URL_PREFIX
import com.gathering.android.common.UserRepo
import com.gathering.android.common.createRequestPartFromUri
import com.gathering.android.common.requestBody
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ApiProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileRemoteService: ProfileRemoteService,
    private val userRepo: UserRepo,
) : ProfileRepository {

    override suspend fun updateProfile(
        displayName: String?,
        photoUri: String?
    ) {
        val filePart = photoUri?.let { context.createRequestPartFromUri(it) }
        if (photoUri?.startsWith(LOCAL_CONTENT_URL_PREFIX) == true) {
            if (filePart == null) {
                throw IllegalStateException()
            }

            val name = displayName?.requestBody()
            val profileResponse = profileRemoteService.uploadProfile(name, filePart)
            userRepo.saveUser(profileResponse.user)
        }
    }
}