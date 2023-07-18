package com.gathering.android.common

import com.gathering.android.event.model.EventModel

data class MyEventResponse(
    override val message: String? = null,
    val eventList: List<EventModel>
) : ApiResponse
