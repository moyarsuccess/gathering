package com.gathering.android.event.putevent.pic

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject

class AddPicViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ViewModel() {

    private val _viewState = ActiveMutableLiveData<AddPicViewState>()
    val viewState: ActiveMutableLiveData<AddPicViewState> by ::_viewState

    fun onCameraClicked() {
        _viewState.setValue(AddPicViewState.ShowCamera)
    }

    fun onGalleryClicked() {
        _viewState.setValue(AddPicViewState.ShowGallery)
    }

    fun onRotateClicked(bitmap: Bitmap, degree: Float) {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
        _viewState.setValue(AddPicViewState.ShowImage(rotatedBitmap))
    }

    fun onOKButtonClicked(bitmap: Bitmap) {
        _viewState.setValue(AddPicViewState.SetResultAndClose(storeBitmapAndGetPath(bitmap)))
    }

    fun onImageSelectedFromGallery(data: Intent?) {
        data?.data?.let { uri ->
            val image = loadBitmap(uri)
            image?.let {
                _viewState.setValue(AddPicViewState.ShowImage(it))
            }
        }
    }

    fun onImageSelectedFromCamera(data: Intent?) {
        val imageBitmap = data?.extras?.get("data") as Bitmap
        _viewState.setValue(AddPicViewState.ShowImage(imageBitmap))
    }

    private fun loadBitmap(imageUri: Uri): Bitmap? {
        return try {
            val contentResolver = context.contentResolver
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        } catch (e: IOException) {
            _viewState.setValue(AddPicViewState.Error("Failed to load image."))
            null
        }
    }

    private fun storeBitmapAndGetPath(inImage: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver, inImage, "gathering_" + System.currentTimeMillis(), null
        )
        return path
    }
}