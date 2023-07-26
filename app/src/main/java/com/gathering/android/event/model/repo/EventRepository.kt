package com.gathering.android.event.model.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.event.model.EventModel

interface EventRepository {

    fun getNextPage(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit)

    fun getFirstPage(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit)

    fun getMyEvents(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit)

    fun getFirstFavoriteEvent(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit)
    fun getNextFavoriteEvent(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit)
    fun likeEvent(
        eventId: Long,
        like: Boolean,
        onResponseReady: (eventRequest: ResponseState<String>) -> Unit
    )
}