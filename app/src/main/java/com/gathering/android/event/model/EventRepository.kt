package com.gathering.android.event.model

import com.gathering.android.common.ResponseState

interface EventRepository {
    fun getAllEvents(onEventRequestReady: (eventRequest: ResponseState) -> Unit)
}