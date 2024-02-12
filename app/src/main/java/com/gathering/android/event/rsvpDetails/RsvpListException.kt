package com.gathering.android.event.rsvpDetails

sealed class RsvpListException : Throwable() {

    data object GeneralException : RsvpListException()
    data object ServerNotRespondingException : RsvpListException()
}