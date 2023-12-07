package com.gathering.android.profile.repo

sealed class ProfileException : Throwable() {

    data object ServerNotRespondingException : ProfileException()

    data object GeneralException : ProfileException()

    data object FileNotFoundException : ProfileException()
}
