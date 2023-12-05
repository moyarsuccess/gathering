package com.gathering.android.event.repo

import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.event.model.EventModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface EventRemoteService {

    @JvmSuppressWildcards
    @Multipart
    @POST("event")
    suspend fun addEvent(
        @Part("event_name") eventName: RequestBody?,
        @Part("event_description") eventDescription: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("date_time") dateTime: RequestBody?,
        @Part("attendees") attendees: RequestBody?,
        @Part photo: MultipartBody.Part,
    ): GeneralApiResponse

    @Multipart
    @PUT("event")
    suspend fun editEvent(
        @Part("event_id") eventId: RequestBody?,
        @Part("event_name") eventName: RequestBody?,
        @Part("event_description") eventDescription: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("date_time") dateTime: RequestBody?,
        @Part("attendees") attendees: RequestBody?,
        @Part photo: MultipartBody.Part?,
    ): GeneralApiResponse

    @GET("all_event")
    suspend fun getAllEvents(
        @Query("page_size") pageSize: Int,
        @Query("page_number") pageNumber: Int,
    ): List<EventModel>

    @GET("my_event")
    suspend fun getMyEvents(
        @Query("page_size") pageSize: Int,
        @Query("page_number") pageNumber: Int,
    ): List<EventModel>

    @PUT("event/like")
    suspend fun likeEvent(
        @Query("event_id") eventId: Long,
        @Query("like") like: Boolean,
    ): GeneralApiResponse

    @DELETE("event")
    suspend fun deleteEvent(
        @Query("event_id") eventId: Long,
    ): GeneralApiResponse

    @GET("liked_event")
    suspend fun getMyLikedEvents(
        @Query("page_size") pageSize: Int,
        @Query("page_number") pageNumber: Int,
    ): List<EventModel>

    @GET("event/id")
    suspend fun getEventById(
        @Query("event_id") eventId: Long,
    ): EventModel
}