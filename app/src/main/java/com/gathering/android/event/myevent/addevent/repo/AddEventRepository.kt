package com.gathering.android.event.myevent.addevent.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.event.Event

interface AddEventRepository {

    fun addEvent(event: Event, onResponseReady: (eventRequest: ResponseState<String>) -> Unit)
}