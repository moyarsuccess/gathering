package com.gathering.android.event.model.repo

import android.util.Log
import com.gathering.android.common.BODY_WAS_NULL
import com.gathering.android.common.MyEventResponse
import com.gathering.android.common.RESPONSE_IS_NOT_SUCCESSFUL
import com.gathering.android.common.ResponseState
import com.gathering.android.common.UserRepo
import com.gathering.android.event.model.EventModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ApiEventRepository @Inject constructor(
    private val eventRemoteService: EventRemoteService,
    private val userRepo: UserRepo,
) : EventRepository {

    override fun getAllEvents(onEventRequestReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getMyEvents(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit) {
        val user = userRepo.getUser()
        eventRemoteService.getEvent(true, user?.email, 1, 15)
            .enqueue(object : Callback<MyEventResponse> {

                override fun onResponse(
                    call: Call<MyEventResponse>,
                    response: Response<MyEventResponse>
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
                    Log.i("WTF", "${body.eventList}")
                    onResponseReady(ResponseState.Success(body.eventList))
                }

                override fun onFailure(call: Call<MyEventResponse>, t: Throwable) {
                    onResponseReady(ResponseState.Failure(t))
                }
            })
    }
}