package com.gathering.android.common

sealed interface RequestState {

    data class Success<T>(val data: T) : RequestState

    data class Failure(val exception: Exception) : RequestState
}