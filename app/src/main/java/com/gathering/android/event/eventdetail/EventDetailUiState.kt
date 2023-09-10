package com.gathering.android.event.eventdetail

data class EventDetailUiState(
    val eventId: Long? = 0,
    val imageUri: String? = null,
    val eventName: String? = null,
    val hostEvent: String? = null,
    val eventDescription: String? = null,
    val eventAddress: String? = null,
    val eventDate: String? = null,
    val eventTime: String? = null,
    val errorMessage: String? = null,
    val acceptButtonBackColor: Int = 0,
    val declineButtonBackColor: Int = 0,
    val maybeButtonBackColor: Int = 0,
)