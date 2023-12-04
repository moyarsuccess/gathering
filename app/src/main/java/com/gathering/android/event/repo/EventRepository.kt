package com.gathering.android.event.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.putevent.PutEventModel

interface EventRepository {

    suspend fun addEvent(event: PutEventModel)
    suspend fun editEvent(event: PutEventModel)

    suspend fun getEvents(page: Int): List<EventModel>

    suspend fun getMyEvents(page: Int): List<EventModel>

    suspend fun getEventById(eventId: Long): EventModel

    fun getMyLikedEvents(
        page: Int,
        onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit
    )

    suspend fun likeEvent(eventId: Long, like: Boolean)

    suspend fun deleteEvent(eventId: Long)
}