package com.gathering.android.event.putevent.pic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject

class BitmapUtility @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun loadBitmap(imageUri: Uri): Bitmap? {
        return try {
            val contentResolver = context.contentResolver
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        } catch (e: IOException) {
            null
        }
    }

    fun storeBitmapAndGetPath(inImage: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        return MediaStore.Images.Media.insertImage(
            context.contentResolver, inImage, "gathering_" + System.currentTimeMillis(), null
        )
    }

    fun rotateBitmap(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}