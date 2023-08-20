package com.gathering.android.event.putevent.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.event.Event

interface PutEventRepository {

    fun addEvent(event: PutEventModel, onResponseReady: (eventRequest: ResponseState<String>) -> Unit)

    fun editEvent(event: PutEventModel, onResponseReady: (eventRequest: ResponseState<String>) -> Unit)
}