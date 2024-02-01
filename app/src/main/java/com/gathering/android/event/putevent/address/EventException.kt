package com.gathering.android.event.putevent.address

sealed class AddressException : Throwable() {

    data object InvalidLocationException : AddressException()
    data object GeneralException : AddressException()

}