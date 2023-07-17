package com.gathering.android.event.myevent.addevent.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.event.model.Event

interface AddEventRepository {

    fun addEvent(event: Event, onEventRequestReady: (eventRequest: ResponseState) -> Unit)

}