package com.gathering.android.event.myevent.addevent.repo

import android.content.Context
import com.gathering.android.common.FAIL_TO_CREATE_FILE_PART
import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.ResponseState
import com.gathering.android.common.createRequestPartFromUri
import com.gathering.android.common.requestBody
import com.gathering.android.event.model.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class ApiAddEventRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val addEventRemoteService: AddEventRemoteService
) : AddEventRepository {

    override fun addEvent(
        event: Event,
        onResponseReady: (eventRequest: ResponseState) -> Unit
    ) {
        val eventName: RequestBody = event.eventName.requestBody()
        val eventDescription: RequestBody = event.description.requestBody()
        val latitude = event.location.lat.requestBody()
        val longitude = event.location.lon.requestBody()
        val dateTime = event.dateAndTime.requestBody()
        val attendees = event.getAttendeesJson().requestBody()
        val filePart = context.createRequestPartFromUri(event.photoUrl)
        if (filePart == null) {
            onResponseReady(ResponseState.Failure(Exception(FAIL_TO_CREATE_FILE_PART)))
            return
        }

        addEventRemoteService.addEvent(
            eventName = eventName,
            eventDescription = eventDescription,
            latitude = latitude,
            longitude = longitude,
            dateTime = dateTime,
            attendees = attendees,
            photo = filePart
        ).enqueue(object : retrofit2.Callback<GeneralApiResponse> {
            override fun onResponse(
                call: Call<GeneralApiResponse>,
                response: Response<GeneralApiResponse>
            ) {
                if (!response.isSuccessful) {
                    onResponseReady(ResponseState.SuccessWithError(response.body()))
                    return
                }
                onResponseReady(ResponseState.Success(response.body()))
            }

            override fun onFailure(call: Call<GeneralApiResponse>, t: Throwable) {
                onResponseReady(ResponseState.Failure(t))
            }
        })
    }
}