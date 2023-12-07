package com.gathering.android.profile.repo

import com.gathering.android.common.UpdateProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ProfileRemoteService {
    @Multipart
    @PUT("profile")
    suspend fun uploadProfile(
        @Part("display_name") displayName: RequestBody?,
        @Part photo: MultipartBody.Part?,
    ): UpdateProfileResponse
}