package com.gathering.android.event.myevent.repo

import com.gathering.android.common.ResponseState
import com.gathering.android.event.myevent.AcceptType

interface AcceptTypeRepository {

    fun setEventAcceptType(
        eventId: Long,
        accept: AcceptType,
        onResponseReady: (ResponseState<String>) -> Unit
    )
}