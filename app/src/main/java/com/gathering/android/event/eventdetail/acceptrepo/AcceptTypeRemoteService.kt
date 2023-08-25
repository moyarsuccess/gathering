package com.gathering.android.event.eventdetail.acceptrepo

import com.gathering.android.common.GeneralApiResponse
import retrofit2.Call
import retrofit2.http.PUT
import retrofit2.http.Query

interface AcceptTypeRemoteService {

    @PUT("event/accept")
    fun setEventAcceptType(
        @Query("event_id") eventId: Long,
        @Query("accept") accept: String
    ): Call<GeneralApiResponse>
}