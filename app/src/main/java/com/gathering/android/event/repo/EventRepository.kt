package com.gathering.android.event.repo

import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.putevent.PutEventModel

interface EventRepository {

    suspend fun addEvent(event: PutEventModel): GeneralApiResponse
    suspend fun editEvent(event: PutEventModel): GeneralApiResponse

    suspend fun getEvents(page: Int): List<EventModel>

    suspend fun getMyEvents(page: Int): List<EventModel>

    suspend fun getEventById(eventId: Long): EventModel

    suspend fun getMyLikedEvents(page: Int): List<EventModel>

    suspend fun likeEvent(eventId: Long, like: Boolean): GeneralApiResponse

    suspend fun deleteEvent(eventId: Long): GeneralApiResponse
}