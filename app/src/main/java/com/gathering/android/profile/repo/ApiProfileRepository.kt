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
            onResponseReady(ResponseState.Failure(IOException(PHOTO_URI_WAS_NOT_VALID)))
            return
        }
        val filePart = MultipartBody.Part.createFormData(
            PHOTO,
            file.absolutePath,
            file.asRequestBody(FILE_PATH.toMediaTypeOrNull())
        )

        val name: RequestBody = displayName.toRequestBody(CONTENT_TYPE.toMediaTypeOrNull())

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
            val tempFile =
                File(context.cacheDir, "${FILE_CHILD}${System.currentTimeMillis()}$FILE_TYPE")
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

    companion object {
        const val PHOTO = "photo"
        const val FILE_PATH = "image/*"
        const val CONTENT_TYPE = "text/plain"
        const val FILE_CHILD = "temp_image_"
        const val FILE_TYPE = ".jpg"
        const val PHOTO_URI_WAS_NOT_VALID = "PHOTO_URI_WAS_NOT_VALID"
        const val BODY_WAS_NULL = "BODY_WAS_NULL"
    }
}