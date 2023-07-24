package com.gathering.android.event.model.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.event.model.EventModel

interface EventRepository {

    fun getAllEvents(onEventRequestReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit)

    fun getMyEvents(onEventRequestReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit)

    fun likeEvent(
        eventId: Long,
        like: Boolean,
        onResponseReady: (eventRequest: ResponseState<String>) -> Unit
    )

}