package com.gathering.android.event

import com.gathering.android.event.model.Attendee
import com.gathering.android.event.model.EventLocation
import java.io.Serializable

data class Event(
    val eventId: Long,
    val eventName: String = "",
    val eventHostEmail: String = "",
    val description: String = "",
    val photoUrl: String = "",
    val location: EventLocation?,
    val dateAndTime: Long = 0,
    val isContactEvent: Boolean = false,
    val isMyEvent: Boolean = false,
    val attendees: List<Attendee> = listOf(),
    val eventCost: Int = 0,
    val liked: Boolean = false,
    val attendeesCount: Int = 0
) : Serializable


