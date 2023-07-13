package com.gathering.android.profile.repo

import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.ResponseState
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject


class ApiProfileRepository @Inject constructor(
    private val profileRemoteService: ProfileRemoteService
) : ProfileRepository {

    override fun updateProfile(
        displayName: String,
        photo: String,
        onResponseReady: (ResponseState) -> Unit
    ) {
        val file = File(photo)
        val filePart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            RequestBody.create(MediaType.parse("image/*"), file)
        )
        profileRemoteService.uploadProfile(displayName, filePart)
            .enqueue(object : Callback<GeneralApiResponse> {
                override fun onResponse(
                    call: Call<GeneralApiResponse>,
                    response: Response<GeneralApiResponse>
                ) {
                    if (response.isSuccessful) {
                        onResponseReady(ResponseState.Success(response.body()))
                    } else {
                        onResponseReady(ResponseState.SuccessWithError(response.body()))
                    }
                }

                override fun onFailure(call: Call<GeneralApiResponse>, t: Throwable) {
                    onResponseReady(ResponseState.Failure(t))
                }
            })
    }
}