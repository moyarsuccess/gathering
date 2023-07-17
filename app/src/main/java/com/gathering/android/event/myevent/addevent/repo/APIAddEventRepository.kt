package com.gathering.android.event.myevent.addevent.repo

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.ResponseState
import com.gathering.android.event.model.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class APIAddEventRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val addEventRemoteService: AddEventRemoteService
) : AddEventRepository {

    override fun addEvent(
        event: Event,
        onResponseReady: (eventRequest: ResponseState) -> Unit
    ) {
        val eventName: RequestBody =
            event.eventName.toRequestBody(CONTENT_TYPE.toMediaTypeOrNull())
        val eventDescription: RequestBody =
            event.description.toRequestBody(CONTENT_TYPE.toMediaTypeOrNull())
        val latitude =
            event.location.lat.toString()
                .toRequestBody(CONTENT_TYPE.toMediaTypeOrNull())
        val longitude = event.location.lon.toString()
            .toRequestBody(CONTENT_TYPE.toMediaTypeOrNull())
        val dateTime =
            event.dateAndTime.toString()
                .toRequestBody(CONTENT_TYPE.toMediaTypeOrNull())

        val attendees =
            event.attendees.map { it.toRequestBody(CONTENT_TYPE.toMediaTypeOrNull()) }

        val file = context.contentResolver.getFileFromContentUri(event.photoUrl)
        if (file == null) {
            onResponseReady(ResponseState.Failure(IOException(PHOTO_URI_WAS_NOT_VALID)))
            return
        }
        val filePart = MultipartBody.Part.createFormData(
            PHOTO,
            file.absolutePath,
            file.asRequestBody(FILE_PATH.toMediaTypeOrNull())
        )

        addEventRemoteService.addEvent(
            eventName = eventName,
            eventDescription = eventDescription,
            latitude = latitude,
            longitude = longitude,
            dateTime = dateTime,
            attendees = attendees,
            photo = filePart
        ).enqueue(object : retrofit2.Callback<GeneralApiResponse> {
            override fun onResponse(
                call: Call<GeneralApiResponse>,
                response: Response<GeneralApiResponse>
            ) {
                if (!response.isSuccessful) {
                    onResponseReady(ResponseState.SuccessWithError(response.body()))
                    return
                }
                onResponseReady(ResponseState.Success(response.body()))
            }

            override fun onFailure(call: Call<GeneralApiResponse>, t: Throwable) {
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
                File(
                    context.cacheDir,
                    "${FILE_CHILD}${System.currentTimeMillis()}${FILE_TYPE}"
                )
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
    }
}