package com.gathering.android.event.putevent.invitation

data class AddAttendeeUiState(
    val attendeeEmail: String? = null,
    val attendeesEmailList: List<String> = listOf(),
    val addAttendeeButtonEnable: Boolean = false,
    val errorMessage: String? = null,
)
