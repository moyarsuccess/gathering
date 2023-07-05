package com.gathering.android.event.model

import com.gathering.android.common.ResponseState

interface EventRepository {

    fun addEvent(event: Event, onEventRequestReady: (eventRequest: ResponseState) -> Unit)
    fun getAllEvents(onEventRequestReady: (eventRequest: ResponseState) -> Unit)
    fun getMyEvents(onEventRequestReady: (eventRequest: ResponseState) -> Unit)
}