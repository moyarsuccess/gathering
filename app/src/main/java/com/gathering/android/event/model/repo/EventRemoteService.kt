package com.gathering.android.event.model.repo

import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.event.model.EventModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface EventRemoteService {

    @GET("event")
    fun getEvents(
        @Query("my_own_events") isMyEvent: Boolean = false,
        @Query("page_size") pageSize: Int,
        @Query("page_number") pageNumber: Int,
    ): Call<List<EventModel>>

    @GET("event")
    fun getMyEvents(
        @Query("my_own_events") isMyEvent: Boolean = true,
        @Query("page_size") pageSize: Int,
        @Query("page_number") pageNumber: Int,
    ): Call<List<EventModel>>

    @PUT("event/like")
    fun likeEvent(
        @Query("event_id") eventId: Long,
        @Query("like") like: Boolean,
    ): Call<GeneralApiResponse>
}