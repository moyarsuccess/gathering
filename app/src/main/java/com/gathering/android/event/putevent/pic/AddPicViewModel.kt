package com.gathering.android.event.putevent.pic

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject

class AddPicViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ViewModel() {

    private var addPicNavigator: AddPicNavigator? = null


    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UiState()
    )

    data class UiState(
        var errorMessage: String? = null,
        var showImage: Bitmap? = null,
        var rotatedImage: Bitmap? = null
    )


    fun onViewCreated(addPicNavigator: AddPicNavigator) {
        this.addPicNavigator = addPicNavigator
    }

    fun onCameraClicked() {
        addPicNavigator?.navigateToCamera()
    }

    fun onImageSelectedFromCamera(bitmap: Bitmap) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(showImage = bitmap)
        }
    }

    fun onGalleryClicked() {
        addPicNavigator?.navigateToGallery()
    }

    fun onImageSelectedFromGallery(data: Intent?) {
        data?.data?.let { uri ->
            val image = loadBitmap(uri)
            if (image != null) {
                viewModelState.update { currentViewState ->
                    currentViewState.copy(showImage = image)
                }
            } else {
                viewModelState.update { currentViewState ->
                    currentViewState.copy(errorMessage = FAILED_TO_LOAD_IMAGE)
                }
            }
        }
    }


    fun onRotateClicked(bitmap: Bitmap, degree: Float) {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        viewModelState.update { currentViewState ->
            currentViewState.copy(showImage = rotatedBitmap)
        }
    }

    fun onSaveButtonClicked(bitmap: Bitmap) {
        val imagePath = storeBitmapAndGetPath(bitmap)
        addPicNavigator?.navigateToAddEvent(imagePath)
    }


    private fun loadBitmap(imageUri: Uri): Bitmap? {
        return try {
            val contentResolver = context.contentResolver
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        } catch (e: IOException) {
            null
        }
    }

    private fun storeBitmapAndGetPath(inImage: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        return MediaStore.Images.Media.insertImage(
            context.contentResolver, inImage, "gathering_" + System.currentTimeMillis(), null
        )
    }

    companion object {
        const val FAILED_TO_LOAD_IMAGE = "FAILED TO LOAD THE IMAGE"
    }
}