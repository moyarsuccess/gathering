package com.gathering.android.event.repo

sealed class EventException : Throwable() {
    data object ServerNotRespondingException : EventException()
    data object LikeEventServerRequestFailedException : EventException()
    data object DeleteEventServerRequestFailedException : EventException()
    class GeneralException(code: Int) : EventException()

}
