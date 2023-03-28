package com.gathering.android.signUp.model

sealed interface ResponseState {

    class Success(val user: User) : ResponseState

    class Failure(val Error: String) : ResponseState
}