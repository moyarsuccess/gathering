package com.gathering.android.event.model

import java.io.Serializable

data class Event(
    val eventName: String,
    val hostName: String,
    val description: String,
    val photoUrl: String,
    val location: EventLocation,
    val dateAndTime: Long,
    val isContactEvent: Boolean = false,
    val isMyEvent: Boolean = false,
    val attendees: List<String> = listOf(),
    val eventCost: Int = 0
) : Serializable