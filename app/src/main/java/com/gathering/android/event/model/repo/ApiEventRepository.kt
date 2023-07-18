package com.gathering.android.event.model.repo

import com.gathering.android.common.BODY_WAS_NULL
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
        onEventRequestReady(ResponseState.Success(listOf()))
    }

    override fun getMyEvents(onResponseReady: (eventRequest: ResponseState<List<EventModel>>) -> Unit) {
        val user = userRepo.getUser()
        eventRemoteService.getMyEvents(pageNumber = 1, pageSize = 15)
            .enqueue(object : Callback<List<EventModel>> {

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
            })
    }
}