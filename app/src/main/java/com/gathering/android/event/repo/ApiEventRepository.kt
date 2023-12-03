package com.gathering.android.event.repo

import android.content.Context
import com.gathering.android.common.BODY_WAS_NULL
import com.gathering.android.common.RESPONSE_IS_NOT_SUCCESSFUL
import com.gathering.android.common.ResponseState
import com.gathering.android.common.createRequestPartFromUri
import com.gathering.android.common.requestBody
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.putevent.repo.PutEventModel
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiEventRepository @Inject constructor(
    private val eventRemoteService: EventRemoteService,
    @ApplicationContext private val context: Context,
) : EventRepository {
    override suspend fun addEvent(event: PutEventModel) {
        val eventName: RequestBody = event.eventName.requestBody()
        val eventDescription: RequestBody = event.description.requestBody()
        val latitude = event.lat.requestBody()
        val longitude = event.lon.requestBody()
        val dateTime = event.dateAndTime.requestBody()
        val attendees = event.getAttendeesJson().requestBody()
        val filePart =
            context.createRequestPartFromUri(event.photoUri) ?: throw IllegalStateException()

        eventRemoteService.addEvent(
            eventName = eventName,
            eventDescription = eventDescription,
            latitude = latitude,
            longitude = longitude,
            dateTime = dateTime,
            attendees = attendees,
            photo = filePart
        )
    }

    override suspend fun editEvent(event: PutEventModel) {
        val eventId: RequestBody = event.eventId.requestBody()
        val eventName: RequestBody = event.eventName.requestBody()
        val eventDescription: RequestBody = event.description.requestBody()
        val latitude = event.lat.requestBody()
        val longitude = event.lon.requestBody()
        val dateTime = event.dateAndTime.requestBody()
        val attendees = event.getAttendeesJson().requestBody()
        val filePart = context.createRequestPartFromUri(event.photoUri)

        eventRemoteService.editEvent(eventId = eventId,
            eventName = eventName,
            eventDescription = eventDescription,
            latitude = latitude,
            longitude = longitude,
            dateTime = dateTime,
            attendees = attendees,
            photo = filePart)
    }

    override suspend fun getEvents(page: Int): List<EventModel> {
        return eventRemoteService.getAllEvents(pageSize = PAGE_SIZE, pageNumber = page)
    }

    override suspend fun getMyEvents(page: Int): List<EventModel> {
        return eventRemoteService.getMyEvents(pageSize = PAGE_SIZE, pageNumber = page)
    }

    override suspend fun likeEvent(
        eventId: Long,
        like: Boolean
    ) {
        eventRemoteService.likeEvent(eventId = eventId, like = like)
    }

    override fun getMyLikedEvents(
        page: Int,
        onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit
    ) {
        eventRemoteService.getMyLikedEvents(pageSize = PAGE_SIZE, pageNumber = page)
            .enqueue(handleGetEventResponse(onResponseReady))
    }

    override suspend fun getEventById(eventId: Long): EventModel {
        return eventRemoteService.getEventById(eventId = eventId)
    }

    private fun handleGetEventResponse(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit) =
        object : Callback<List<EventModel>> {
            override fun onResponse(
                call: Call<List<EventModel>>,
                response: Response<List<EventModel>>
            ) {
                if (!response.isSuccessful) {
                    onResponseReady(
                        ResponseState.Failure(
                            Exception(RESPONSE_IS_NOT_SUCCESSFUL)
                        )
                    )
                    return
                }
                val body = response.body()
                if (body == null) {
                    onResponseReady(ResponseState.Failure(Exception(BODY_WAS_NULL)))
                    return
                }
                onResponseReady(ResponseState.Success(body))
            }

            override fun onFailure(call: Call<List<EventModel>>, t: Throwable) {
                onResponseReady(ResponseState.Failure(t))
            }
        }

    override suspend fun deleteEvent(eventId: Long) {
        eventRemoteService.deleteEvent(eventId)
    }

    companion object {
        const val PAGE_SIZE = 15
    }
}