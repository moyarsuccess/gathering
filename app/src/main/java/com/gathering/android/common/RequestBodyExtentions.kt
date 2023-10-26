package com.gathering.android.common

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

const val FAIL_TO_CREATE_FILE_PART = "Fail to create the file part"
private const val CONTENT_TYPE = "text/plain"
private const val FILE_PATH = "image/*"
private const val TEMP_FILE_NAME_PREFIX = "temp_image_"
private const val JPEG_FILE_TYPE = "jpg"

fun Context.createRequestPartFromUri(photoUri: String?): MultipartBody.Part? {
    try {
        val contentUri = Uri.parse(photoUri)
        val tempFile = createJpegTempFile()
        FileOutputStream(tempFile).use { outputStream ->
            contentResolver.openInputStream(contentUri)?.use { input ->
                val buffer = ByteArray(4 * 1024) // 4k buffer
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                return MultipartBody.Part.createFormData(
                    PHOTO,
                    tempFile.absolutePath,
                    tempFile.asRequestBody(FILE_PATH.toMediaTypeOrNull())
                )
            }
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun String.requestBody(): RequestBody {
    return toRequestBody(CONTENT_TYPE.toMediaTypeOrNull())
}

fun Long.requestBody(): RequestBody {
    return toString().requestBody()
}

fun Double.requestBody(): RequestBody {
    return toString().requestBody()
}

private fun Context.createJpegTempFile(): File {
    return File(
        cacheDir,
        "${TEMP_FILE_NAME_PREFIX}${System.currentTimeMillis()}.${JPEG_FILE_TYPE}"
    )
}


