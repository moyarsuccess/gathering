package com.gathering.android.event.putevent

data class PutEventUiState(
    val imageUri: String? = null,
    val eventName: String? = null,
    val eventDescription: String? = null,
    val eventDate: String? = null,
    val eventTime: String? = null,
    val eventAddress: String? = null,
    val eventAttendees: String? = null,
    val actionButtonText: String? = null,
    val errorMessage: String? = null,
    val showProgress: Boolean? = false,
    val actionButtonEnable: Boolean? = false,
)