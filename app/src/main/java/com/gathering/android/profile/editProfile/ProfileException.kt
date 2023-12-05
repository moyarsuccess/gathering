package com.gathering.android.profile.editProfile

sealed class ProfileException : Throwable() {
    data object ServerNotRespondingException : ProfileException()
    class GeneralException(code: Int) : ProfileException()
}