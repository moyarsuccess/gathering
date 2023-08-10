package com.gathering.android.event.putevent.repo

import android.content.Context
import com.gathering.android.common.*
import com.gathering.android.common.requestBody
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiPutEventRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val putEventRemoteService: PutEventRemoteService
) : PutEventRepository {

    override fun addEvent(
        event: PutEventModel,
        onResponseReady: (eventRequest: ResponseState<String>) -> Unit
    ) {
        val eventName: RequestBody = event.eventName.requestBody()
        val eventDescription: RequestBody = event.description.requestBody()
        val latitude = event.lat.requestBody()
        val longitude = event.lon.requestBody()
        val dateTime = event.dateAndTime.requestBody()
        val attendees = event.getAttendeesJson().requestBody()
        val filePart = context.createRequestPartFromUri(event.photoUri)
        if (filePart == null) {
            onResponseReady(ResponseState.Failure(Exception(FAIL_TO_CREATE_FILE_PART)))
            return
        }

        putEventRemoteService.addEvent(
            eventName = eventName,
            eventDescription = eventDescription,
            latitude = latitude,
            longitude = longitude,
            dateTime = dateTime,
            attendees = attendees,
            photo = filePart
        ).enqueue(object : Callback<GeneralApiResponse> {
            override fun onResponse(
                call: Call<GeneralApiResponse>,
                response: Response<GeneralApiResponse>
            ) {
                if (!response.isSuccessful) {
                    onResponseReady(
                        ResponseState.Failure(
                            Exception(RESPONSE_IS_NOT_SUCCESSFUL)
                        )
                    )
                    return
                }
                onResponseReady(ResponseState.Success(response.body()?.message ?: ""))
            }

            override fun onFailure(call: Call<GeneralApiResponse>, t: Throwable) {
                onResponseReady(ResponseState.Failure(t))
            }
        })
    }

    override fun editEvent(
        event: PutEventModel,
        onResponseReady: (eventRequest: ResponseState<String>) -> Unit
    ) {
        val eventName: RequestBody = event.eventName.requestBody()
        val eventDescription: RequestBody = event.description.requestBody()
        val latitude = event.lat.requestBody()
        val longitude = event.lon.requestBody()
        val dateTime = event.dateAndTime.requestBody()
        val attendees = event.getAttendeesJson().requestBody()
        val filePart = context.createRequestPartFromUri(event.photoUri)
        if (filePart == null) {
            onResponseReady(ResponseState.Failure(Exception(FAIL_TO_CREATE_FILE_PART)))
            return
        }

        putEventRemoteService.editEvent(
            eventName = eventName,
            eventDescription = eventDescription,
            latitude = latitude,
            longitude = longitude,
            dateTime = dateTime,
            attendees = attendees,
            photo = filePart
        ).enqueue(object : Callback<GeneralApiResponse> {
            override fun onResponse(
                call: Call<GeneralApiResponse>,
                response: Response<GeneralApiResponse>
            ) {
                if (!response.isSuccessful) {
                    onResponseReady(
                        ResponseState.Failure(
                            Exception(RESPONSE_IS_NOT_SUCCESSFUL)
                        )
                    )
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