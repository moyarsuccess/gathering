package com.gathering.android.event

import com.gathering.android.event.model.Attendee
import java.io.Serializable

data class Event(
    val eventId: Long,
    val eventName: String = "",
    val eventHostEmail: String = "",
    val description: String = "",
    val photoUrl: String = "",
    var latitude: Double?,
    var longitude: Double?,
    val dateAndTime: Long = 0,
    val isContactEvent: Boolean = false,
    val isMyEvent: Boolean = false,
    val attendees: List<Attendee> = listOf(),
    val liked: Boolean = false,
) : Serializable {
    companion object {
        fun getDefault() = Event(
            eventId = -1,
            latitude = 0.0,
            longitude = 0.0
        )
    }
}
