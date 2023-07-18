package com.gathering.android.event

import com.gathering.android.event.model.EventLocation
import com.gathering.android.event.model.EventModel

fun EventModel.toEvent(): Event {
    return Event(
        eventName = this.eventName ?: "",
        eventHostEmail = this.eventHostEmail ?: "",
        description = this.eventDescription ?: "",
        photoUrl = this.photoName ?: "",
        location = EventLocation(this.latitude, this.longitude),
        dateAndTime = this.dateTime?.toLong() ?: 0,
        isContactEvent = false,
        isMyEvent = true,
        attendees = this.attendees.map { attendee -> attendee.email ?: "" },
    )
}