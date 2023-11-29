package com.gathering.android.event.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.event.model.EventModel
import kotlinx.coroutines.CoroutineExceptionHandler

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

    suspend fun getEvents(page: Int, exceptionHandler: CoroutineExceptionHandler): List<EventModel>

    fun getMyLikedEvents(
        page: Int,
        onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit
    )

    suspend fun getEventById(eventId: Long): EventModel
}