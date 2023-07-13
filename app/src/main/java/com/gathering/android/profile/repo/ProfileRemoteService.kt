package com.gathering.android.profile.repo

import com.gathering.android.common.GeneralApiResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ProfileRemoteService {

    @Multipart
    @PUT("profile")
    fun uploadProfile(
        @Part displayName: String,
        @Part photoFile: MultipartBody.Part,
    ): Call<GeneralApiResponse>
}