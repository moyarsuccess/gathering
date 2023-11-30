package com.gathering.android.event.eventdetail.acceptrepo

import com.gathering.android.common.ResponseState
import com.gathering.android.event.eventdetail.AcceptType

interface AttendanceStateRepository {
    fun setEventAcceptType(
        eventId: Long,
        accept: AcceptType,
        onResponseReady: (ResponseState<String>) -> Unit
    )
}