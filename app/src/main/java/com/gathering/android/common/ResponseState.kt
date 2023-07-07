package com.gathering.android.common

sealed interface ResponseState {

    data class Success<T>(val data: T) : ResponseState

    data class SuccessWithError<T>(val data: T) : ResponseState

    data class Failure(val throwable: Throwable) : ResponseState
}