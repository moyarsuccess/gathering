package com.gathering.android.event.model

sealed interface EventRequest {

    data class Success<T>(val data: T) : EventRequest

    data class Failure(val exception: Exception) : EventRequest
}