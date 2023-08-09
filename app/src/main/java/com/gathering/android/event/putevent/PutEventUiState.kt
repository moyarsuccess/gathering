package com.gathering.android.event.putevent

data class PutEventUiState(
    val phoneImageUri: String? = null,
    val networkImageUri: String? = null,
    val btnText: String = "",
    val eventName: String = "",
    val eventDescription: String = "",
    val address: String = "",
    val attendees: String = "",
    val date: String = "",
    val time: String = "",
    val enableActionButton: Boolean = false,
    val showProgress: Boolean = false,
    val errorMessage: String? = null,
)