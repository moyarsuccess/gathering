package com.gathering.android.event.model

interface EventRepository {

    fun addEvent(event: Event, onEventRequestReady: (eventRequest: EventRequest) -> Unit)
    fun getAllEvents(onEventRequestReady: (eventRequest: EventRequest) -> Unit)
    fun getMyEvents(onEventRequestReady: (eventRequest: EventRequest) -> Unit)
}