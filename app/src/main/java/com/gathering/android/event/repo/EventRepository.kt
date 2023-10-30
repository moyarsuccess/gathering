package com.gathering.android.event.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.event.model.EventModel

interface EventRepository {

    fun likeEvent(
        eventId: Long, like: Boolean, onResponseReady: (eventRequest: ResponseState<String>) -> Unit
    )

    fun deleteEvent(
        eventId: Long, onResponseReady: (eventRequest: ResponseState<String>) -> Unit
    )

    fun getMyEvents(
        page: Int,
        onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit
    )

    fun getEvents(
        page: Int,
        onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit
    )
    fun likedMyEvents(
        page: Int,
        onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit
    )
}