package com.gathering.android.event.model.repo

import com.gathering.android.event.model.EventModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EventRemoteService {

    @GET("event")
    fun getEvents(
        @Query("my_own_events") isMyEvent: Boolean,
        @Query("host_email") hostEmail: String?,
        @Query("page_size") pageSize: Int,
        @Query("page_number") pageNumber: Int,
    ): Call<List<EventModel>>

    @GET("event")
    fun getMyEvents(
        @Query("my_own_events") isMyEvent: Boolean = true,
        @Query("page_size") pageSize: Int,
        @Query("page_number") pageNumber: Int,
    ): Call<List<EventModel>>
}