package com.gathering.android.event.putevent.pic

import android.content.Intent
import android.graphics.Bitmap
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.event.GENERAL_ERROR
import com.gathering.android.event.IMAGE_IS_NULL_OR_INVALID
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddPicViewModel @Inject constructor(
    private val bitmapUtility: BitmapUtility
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var addPicNavigator: AddPicNavigator? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is AddPicException -> {
                when (throwable) {
                    AddPicException.InvalidImageException -> IMAGE_IS_NULL_OR_INVALID
                    is AddPicException.GeneralException -> GENERAL_ERROR
                }
            }

            else -> {
                GENERAL_ERROR
            }
        }
        viewModelState.update { currentState ->
            currentState.copy(
                errorMessage = errorMessage,
            )
        }
    }

    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UiState()
    )

    fun onViewCreated(addPicNavigator: AddPicNavigator) {
        this.addPicNavigator = addPicNavigator
    }

    fun onCameraClicked() {
        addPicNavigator?.navigateToCamera()
    }

    fun onImageSelectedFromCamera(bitmap: Bitmap) {
        viewModelScope.launch(exceptionHandler) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(showImage = bitmap)
            }
        }
    }

    fun onGalleryClicked() {
        addPicNavigator?.navigateToGallery()
    }

    fun onImageSelectedFromGallery(data: Intent?) {
        viewModelScope.launch(exceptionHandler) {
            if (data == null) return@launch
            val uri = data.data ?: return@launch
            val image = bitmapUtility.loadBitmap(uri)
            if (image == null) {
                viewModelState.update { currentViewState ->
                    currentViewState.copy(errorMessage = FAILED_TO_LOAD_IMAGE)
                }
            } else {
                viewModelState.update { currentViewState ->
                    currentViewState.copy(showImage = image)
                }
            }
        }
    }


    fun onRotateClicked(bitmap: Bitmap, degree: Float) {
        val rotatedBitmap = bitmapUtility.rotateBitmap(bitmap, degree)
        viewModelScope.launch(exceptionHandler) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(showImage = rotatedBitmap)
            }
        }
    }

    fun onSaveButtonClicked(bitmap: Bitmap) {
        val imagePath = bitmapUtility.storeBitmapAndGetPath(bitmap)
        addPicNavigator?.navigateToAddEvent(imagePath)
    }

    data class UiState(
        var errorMessage: String? = null,
        var showImage: Bitmap? = null,
        var rotatedImage: Bitmap? = null
    )

    companion object {
        const val FAILED_TO_LOAD_IMAGE = "FAILED TO LOAD THE IMAGE"
    }
}