package com.gathering.android.event.eventdetail.acceptrepo

import com.gathering.android.event.eventdetail.AcceptType
import javax.inject.Inject

class ApiAttendanceStateRepository @Inject constructor(
    private val acceptTypeRemoteService: AcceptTypeRemoteService
) : AttendanceStateRepository {
    override suspend fun setEventAcceptType(eventId: Long, accept: AcceptType) {
        acceptTypeRemoteService.setEventAcceptType(eventId = eventId, accept = accept.type)
    }
}