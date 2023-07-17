package com.gathering.android.profile.repo

import android.content.Context
import android.util.Log
import com.gathering.android.common.FAIL_TO_CREATE_FILE_PART
import com.gathering.android.common.ResponseState
import com.gathering.android.common.UpdateProfileResponse
import com.gathering.android.common.UserRepo
import com.gathering.android.common.createRequestPartFromUri
import com.gathering.android.common.requestBody
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class ApiProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileRemoteService: ProfileRemoteService,
    private val userRepo: UserRepo,
) : ProfileRepository {

    override fun updateProfile(
        displayName: String,
        photoUri: String,
        onResponseReady: (ResponseState) -> Unit
    ) {
        val filePart = context.createRequestPartFromUri(photoUri)
        if (filePart == null) {
            onResponseReady(ResponseState.Failure(IOException(FAIL_TO_CREATE_FILE_PART)))
            return
        }

        val name: RequestBody = displayName.requestBody()

        profileRemoteService.uploadProfile(name, filePart)
            .enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    if (!response.isSuccessful) {
                        onResponseReady(ResponseState.SuccessWithError(response.body()))
                        return
                    }
                    val body = response.body()
                    if (body == null) {
                        onResponseReady(ResponseState.SuccessWithError(Exception(BODY_WAS_NULL)))
                        return
                    }
                    userRepo.saveUser(body.user)
                    Log.i("WTF", "${body.user}")
                    onResponseReady(ResponseState.Success(body))
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    onResponseReady(ResponseState.Failure(t))
                }
            })
    }

    companion object {
        const val BODY_WAS_NULL = "BODY_WAS_NULL"
    }
}