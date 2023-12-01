package com.gathering.android.event.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.event.model.EventModel

interface EventRepository {

    suspend fun getEvents(page: Int): List<EventModel>

    fun getMyEvents(
        page: Int,
        onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit
    )

    suspend fun getEventById(eventId: Long): EventModel

    fun getMyLikedEvents(
        page: Int,
        onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit
    )

    fun likeEvent(
        eventId: Long, like: Boolean, onResponseReady: (eventRequest: ResponseState<String>) -> Unit
    )

    fun deleteEvent(
        eventId: Long, onResponseReady: (eventRequest: ResponseState<String>) -> Unit
    )
}