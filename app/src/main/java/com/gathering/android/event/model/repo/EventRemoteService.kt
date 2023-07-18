package com.gathering.android.event.model.repo

import com.gathering.android.common.MyEventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EventRemoteService {

    @GET("event")
    fun getEvent(
        @Query("my_own_events") isMyEvent: Boolean,
        @Query("host_email") hostEmail: String?,
        @Query("page_size") pageSize: Int,
        @Query("page_number") pageNumber: Int,
    ): Call<MyEventResponse>
}