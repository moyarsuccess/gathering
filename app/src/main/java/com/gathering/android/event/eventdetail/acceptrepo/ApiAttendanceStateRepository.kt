package com.gathering.android.event.eventdetail.acceptrepo

import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.RESPONSE_IS_NOT_SUCCESSFUL
import com.gathering.android.common.ResponseState
import com.gathering.android.event.eventdetail.AcceptType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiAttendanceStateRepository @Inject constructor(
    private val acceptTypeRemoteService: AcceptTypeRemoteService
) : AttendanceStateRepository {

    override fun setEventAcceptType(
        eventId: Long, accept: AcceptType, onResponseReady: (ResponseState<String>) -> Unit
    ) {
        acceptTypeRemoteService.setEventAcceptType(eventId, accept.type)
            .enqueue(object : Callback<GeneralApiResponse> {
                override fun onResponse(
                    call: Call<GeneralApiResponse>, response: Response<GeneralApiResponse>
                ) {
                    if (!response.isSuccessful) {
                        onResponseReady(ResponseState.Failure(Exception(RESPONSE_IS_NOT_SUCCESSFUL)))
                        return
                    }
                    onResponseReady(ResponseState.Success(response.body()?.message ?: ""))
                }

                override fun onFailure(call: Call<GeneralApiResponse>, t: Throwable) {
                    onResponseReady(ResponseState.Failure(t))
                }
            })
    }
}