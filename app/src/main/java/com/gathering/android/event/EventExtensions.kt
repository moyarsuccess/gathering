package com.gathering.android.event

import com.gathering.android.event.model.EventModel

fun EventModel.toEvent(): Event {
    return Event(
        eventId = this.id,
        eventName = this.eventName ?: "",
        eventHostEmail = this.eventHostEmail ?: "",
        description = this.eventDescription ?: "",
        photoUrl = this.photoName ?: "",
        latitude = this.latitude,
        longitude = this.longitude,
        dateAndTime = this.dateTime ?: 0,
        isContactEvent = false,
        isMyEvent = true,
        attendees = this.attendees,
        liked = this.liked,
    )
}