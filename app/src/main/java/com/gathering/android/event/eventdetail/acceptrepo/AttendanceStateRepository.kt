package com.gathering.android.event.eventdetail.acceptrepo

import com.gathering.android.event.eventdetail.AcceptType

interface AttendanceStateRepository {
    suspend fun setEventAcceptType(
        eventId: Long,
        accept: AcceptType,
    )
}