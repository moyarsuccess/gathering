package com.gathering.android.event.putevent.pic

import android.graphics.Bitmap

sealed interface AddPicViewState {

    object ShowCamera : AddPicViewState

    object ShowGallery : AddPicViewState

    class ShowImage(val image: Bitmap) : AddPicViewState

    class Error(val error: String) : AddPicViewState

    class SetResultAndClose(val imagePath: String) : AddPicViewState
}