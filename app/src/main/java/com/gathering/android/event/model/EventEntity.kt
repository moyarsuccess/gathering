package com.gathering.android.event.model

import java.io.Serializable

data class EventEntity(
    val eventName: String = "",
    val hostName: String = "",
    val description: String = "",
    val photoUrl: String = "",
    val location: EventLocation = EventLocation(0.0, 0.0, ""),
    val timeStamp: Long = 0,
    val isInFriendZoneEvent: Boolean = false,
    val isMyEvent: Boolean = false,
    val attendees: List<String> = listOf(),
    val eventCost: Int = 0
) : Serializable
