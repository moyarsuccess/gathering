package com.gathering.android.profile.repo

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.gathering.android.common.ResponseState
import com.gathering.android.common.UpdateProfileResponse
import com.gathering.android.common.UserRepo
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
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
        val file = context.contentResolver.getFileFromContentUri(photoUri)
        if (file == null) {
            onResponseReady(ResponseState.Failure(IOException("Photo Uri was not valid")))
            return
        }
        val filePart = MultipartBody.Part.createFormData(
            "photo",
            file.absolutePath,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )

        val name: RequestBody = displayName.toRequestBody("text/plain".toMediaTypeOrNull())

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
                        onResponseReady(ResponseState.SuccessWithError(Exception("Body was null")))
                        return
                    }
                    userRepo.saveUser(body.user)
                    onResponseReady(ResponseState.Success(body))
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    onResponseReady(ResponseState.Failure(t))
                }
            })
    }

    private fun ContentResolver.getFileFromContentUri(photoUri: String): File? {
        val contentUri = Uri.parse(photoUri)
        val inputStream = openInputStream(contentUri)
        var outputStream: FileOutputStream? = null

        try {
            val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            outputStream = FileOutputStream(tempFile)
            inputStream?.use { input ->
                val buffer = ByteArray(4 * 1024) // 4k buffer
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                return tempFile
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
        return null
    }
}