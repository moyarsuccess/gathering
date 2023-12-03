package com.gathering.android.event.repo

import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.event.model.EventModel
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface EventRemoteService {

    @GET("all_event")
    suspend fun getAllEvents(
        @Query("page_size") pageSize: Int,
        @Query("page_number") pageNumber: Int,
    ): List<EventModel>

    @GET("my_event")
    fun getMyEvents(
        @Query("page_size") pageSize: Int,
        @Query("page_number") pageNumber: Int,
    ): Call<List<EventModel>>

    @GET("my_event")
    suspend fun getMyEvents2(
        @Query("page_size") pageSize: Int,
        @Query("page_number") pageNumber: Int,
    ): List<EventModel>

    @PUT("event/like")
    suspend fun likeEvent(
        @Query("event_id") eventId: Long,
        @Query("like") like: Boolean,
    ): GeneralApiResponse

    @DELETE("event")
    fun deleteEvent(
        @Query("event_id") eventId: Long,
    ): Call<GeneralApiResponse>

    @DELETE("event")
   suspend fun deleteEvent2(
        @Query("event_id") eventId: Long,
    ): GeneralApiResponse

    @GET("my_event")
    fun getMyLikedEvents(
        @Query("page_size") pageSize: Int,
        @Query("page_number") pageNumber: Int,
    ): Call<List<EventModel>>

    @GET("event/id")
    suspend fun getEventById(
        @Query("event_id") eventId: Long,
    ): EventModel
}