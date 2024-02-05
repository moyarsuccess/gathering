package com.gathering.android.event.putevent.pic

sealed class AddPicException : Throwable() {

    data object InvalidImageException : AddPicException()
    data object GeneralException : AddPicException()

}