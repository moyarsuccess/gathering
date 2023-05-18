package com.gathering.android.auth.model

sealed interface ResponseState {

    class Success(val user: User) : ResponseState

    class Failure(val Error: String) : ResponseState
}