package com.gathering.android.profile.repo

import android.content.Context
import com.gathering.android.common.LOCAL_CONTENT_URL_PREFIX
import com.gathering.android.common.UpdateProfileResponse
import com.gathering.android.common.UserRepository
import com.gathering.android.common.createRequestPartFromUri
import com.gathering.android.common.requestBody
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApiProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileRemoteService: ProfileRemoteService,
    private val userRepository: UserRepository,
) : ProfileRepository {

    override suspend fun updateProfile(
        displayName: String?,
        photoUri: String?
    ): UpdateProfileResponse? = withContext(Dispatchers.IO) {
        if (displayName == null && photoUri == null) return@withContext null
        val filePart = photoUri?.let { context.createRequestPartFromUri(it) }

        if (
            photoUri?.startsWith(LOCAL_CONTENT_URL_PREFIX) == true
            && filePart == null
        ) throw ProfileException.FileNotFoundException

        val name = displayName?.requestBody()
        val profileResponse = profileRemoteService.uploadProfile(name, filePart)
        userRepository.saveUser(profileResponse.user)
        profileResponse
    }
}