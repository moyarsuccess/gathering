package com.gathering.android.event.myevent.addevent.repo

import com.gathering.android.common.GeneralApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AddEventRemoteService {

    @JvmSuppressWildcards
    @Multipart
    @POST("event")
    fun addEvent(
        @Part("event_name") eventName: RequestBody?,
        @Part("event_description") eventDescription: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("date_time") dateTime: RequestBody?,
        @Part("attendees") attendees: RequestBody?,
        @Part photo: MultipartBody.Part,
    ): Call<GeneralApiResponse>
}