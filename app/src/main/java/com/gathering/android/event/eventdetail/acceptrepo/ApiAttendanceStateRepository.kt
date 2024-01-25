package com.gathering.android.event.eventdetail.acceptrepo

import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.event.eventdetail.AcceptType
import com.gathering.android.event.repo.EventException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class ApiAttendanceStateRepository @Inject constructor(
    private val acceptTypeRemoteService: AcceptTypeRemoteService
) : AttendanceStateRepository {
    override suspend fun setEventAcceptType(eventId: Long, accept: AcceptType): GeneralApiResponse =
        withContext(Dispatchers.IO) {
            try {
                acceptTypeRemoteService.setEventAcceptType(eventId = eventId, accept = accept.type)
            } catch (e: HttpException) {
                val throwable = when (e.code()) {
                    CAN_NOT_REACH_SERVER -> EventException.NotAbleToCatchAttendanceResponseException
                    else -> EventException.GeneralException
                }
                throw throwable
            }
        }

    companion object {
        private const val CAN_NOT_REACH_SERVER = 503
    }
}