package com.gathering.android.event.model.repo

import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.event.model.EventModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

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

    @DELETE("event")
    fun deleteEvent(
        @Query("event_id") eventId: Long,
    ): Call<GeneralApiResponse>

    @Multipart
    @PUT("event")
    fun editEvent(
        @Part("event_name") eventName: RequestBody?,
        @Part("event_description") eventDescription: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("date_time") dateTime: RequestBody?,
        @Part("attendees") attendees: RequestBody?,
        @Part photo: MultipartBody.Part,
    ): Call<GeneralApiResponse>
}