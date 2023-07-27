package com.gathering.android.event.model.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.event.Event
import com.gathering.android.event.model.EventModel

interface EventRepository {

    fun getNextPage(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit)

    fun getFirstPage(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit)

    fun getMyEvents(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit)

    fun likeEvent(
        eventId: Long, like: Boolean, onResponseReady: (eventRequest: ResponseState<String>) -> Unit
    )

    fun deleteEvent(
        eventId: Long, onResponseReady: (eventRequest: ResponseState<String>) -> Unit
    )

    fun editEvent(
        event: Event,
        onResponseReady: (eventRequest: ResponseState<String>) -> Unit
    )
}