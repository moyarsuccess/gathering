package com.gathering.android.common

sealed interface ResponseState<T> {

    data class Success<T>(val data: T) : ResponseState<T>

    data class Failure<T>(val throwable: Throwable) : ResponseState<T>
}