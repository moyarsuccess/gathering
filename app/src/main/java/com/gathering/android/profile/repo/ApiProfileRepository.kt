package com.gathering.android.profile.repo

import android.content.Context
import android.util.Log
import com.gathering.android.common.BODY_WAS_NULL
import com.gathering.android.common.FAIL_TO_CREATE_FILE_PART
import com.gathering.android.common.LOCAL_CONTENT_URL_PREFIX
import com.gathering.android.common.RESPONSE_IS_NOT_SUCCESSFUL
import com.gathering.android.common.ResponseState
import com.gathering.android.common.UpdateProfileResponse
import com.gathering.android.common.UserRepo
import com.gathering.android.common.createRequestPartFromUri
import com.gathering.android.common.requestBody
import dagger.hilt.android.qualifiers.ApplicationContext
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
        displayName: String?,
        photoUri: String?,
        onResponseReady: (ResponseState<UpdateProfileResponse>) -> Unit
    ) {
        val filePart = photoUri?.let { context.createRequestPartFromUri(it) }
        if (photoUri?.startsWith(LOCAL_CONTENT_URL_PREFIX) == true) {
            if (filePart == null) {
                onResponseReady(ResponseState.Failure(IOException(FAIL_TO_CREATE_FILE_PART)))
                return
            }
        }

        val name = displayName?.requestBody()

        profileRemoteService.uploadProfile(name, filePart)
            .enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    if (!response.isSuccessful) {
                        onResponseReady(
                            ResponseState.Failure(
                                Exception(
                                    RESPONSE_IS_NOT_SUCCESSFUL
                                )
                            )
                        )
                        return
                    }
                    val body = response.body()
                    if (body == null) {
                        onResponseReady(ResponseState.Failure(Exception(BODY_WAS_NULL)))
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
}