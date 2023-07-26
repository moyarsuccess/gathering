package com.gathering.android.event.model.repo

import com.gathering.android.common.BODY_WAS_NULL
import com.gathering.android.common.GeneralApiResponse
import com.gathering.android.common.RESPONSE_IS_NOT_SUCCESSFUL
import com.gathering.android.common.ResponseState
import com.gathering.android.event.model.Attendees
import com.gathering.android.event.model.EventModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiEventRepository @Inject constructor(
    private val eventRemoteService: EventRemoteService,
) : EventRepository {

    private var pageNumber = 1

    override fun getFirstPage(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit) {
        eventRemoteService.getEvents(
            pageSize = PAGE_SIZE,
            pageNumber = pageNumber
        ).enqueue(handleGetEventResponse(onResponseReady))
    }

    override fun getNextPage(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit) {
        pageNumber++
        eventRemoteService.getEvents(
            pageSize = PAGE_SIZE,
            pageNumber = pageNumber
        ).enqueue(handleGetEventResponse(onResponseReady))
    }

    override fun getMyEvents(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit) {
        eventRemoteService.getMyEvents(pageSize = PAGE_SIZE, pageNumber = 1)
            .enqueue(handleGetEventResponse(onResponseReady))
    }

    override fun getFirstFavoriteEvent(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit) {
        onResponseReady(ResponseState.Success(provideMockData()))
    }

    override fun getNextFavoriteEvent(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit) {
        onResponseReady(ResponseState.Success(provideMockData()))
    }

    override fun likeEvent(
        eventId: Long,
        like: Boolean,
        onResponseReady: (eventRequest: ResponseState<String>) -> Unit
    ) {
        eventRemoteService.likeEvent(eventId, like).enqueue(object : Callback<GeneralApiResponse> {
            override fun onResponse(
                call: Call<GeneralApiResponse>,
                response: Response<GeneralApiResponse>
            ) {
                if (!response.isSuccessful) {
                    onResponseReady(ResponseState.Failure(Exception(RESPONSE_IS_NOT_SUCCESSFUL)))
                    return
                }
                onResponseReady(ResponseState.Success(response.body()?.message ?: ""))
            }

            override fun onFailure(call: Call<GeneralApiResponse>, t: Throwable) {
                onResponseReady(ResponseState.Failure(t))
            }
        })
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

    private fun provideMockData(): List<EventModel> {
        val attendee1 = Attendees(
            id = "1",
            email = "animansoubi@gmail.com",
            displayName = "animan",
            imageName = "268e332c-517b-4ff2-8eb8-4ab1ea2343f8.jpg",
        )
        val event1 = EventModel(
            id = 7,
            eventName = "Test1",
            eventHostEmail = "animansoubi@gmail.com",
            eventDescription = "This is my test1 event",
            photoName = "cc3ba2c0-725a-4b96-a78b-7465ccfdaafe.jpg",
            latitude = 43.6585076,
            longitude = -79.3822429,
            dateTime = 1690538340342,
            attendees = arrayListOf(attendee1),
            liked = false
        )
        return listOf(event1)
    }

    companion object {
        const val PAGE_SIZE = 5
    }
}