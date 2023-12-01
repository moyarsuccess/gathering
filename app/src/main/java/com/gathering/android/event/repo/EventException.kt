package com.gathering.android.event.repo

sealed class EventException : Throwable() {
    data object ServerNotRespondingException : EventException()
    data object LikeEventServerRequestFailedException : EventException()
    class GeneralException(code: Int) : EventException()
}
