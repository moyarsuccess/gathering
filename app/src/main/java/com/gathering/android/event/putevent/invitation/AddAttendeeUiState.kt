package com.gathering.android.event.putevent.invitation

data class AddAttendeeUiState(
    val attendeesEmailList: List<String> = listOf(),
    val addAttendeeButtonEnable: Boolean = false,
)
