package com.gathering.android.event.myevent

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.ResponseState
import com.gathering.android.event.Event
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.model.repo.EventRepository
import com.gathering.android.event.toEvent
import javax.inject.Inject

class MyEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _viewState = ActiveMutableLiveData<MyEventViewState>()
    val viewState: MutableLiveData<MyEventViewState> by ::_viewState

    private var deletedEvent: Event? = null

    fun onViewCreated() {
        _viewState.setValue(MyEventViewState.ShowProgress)
        eventRepository.getMyEvents { request ->
            when (request) {
                is ResponseState.Failure -> {
                    Log.d("WTF_SWE", request.throwable.message ?: "")
                    _viewState.setValue(MyEventViewState.ShowNoData)
                    hideProgress()
                }

                is ResponseState.Success<List<EventModel>> -> {
                    val eventModelList = request.data as? List<EventModel>
                    (eventModelList)
                        ?.map { it.toEvent() }
                        ?.also { events ->
                            if (events.isEmpty()) {
                                _viewState.setValue(MyEventViewState.ShowNoData)
                                hideProgress()
                                return@also
                            }
                            _viewState.setValue(MyEventViewState.ShowUserEventList(events))
                            _viewState.setValue(MyEventViewState.HideNoData)
                            hideProgress()
                        } ?: run {
                        _viewState.setValue(MyEventViewState.ShowNoData)
                        hideProgress()
                    }
                }
            }
        }
    }

    fun onEventLikeClicked(event: Event) {
        val liked = !event.liked
        val eventId = event.eventId
        eventRepository.likeEvent(eventId, liked) { request ->
            when (request) {
                is ResponseState.Failure -> {
                    _viewState.setValue(MyEventViewState.ShowError(LIKE_EVENT_REQUEST_FAILED))
                }

                is ResponseState.Success -> {
                    _viewState.setValue(MyEventViewState.UpdateEvent(event.copy(liked = !event.liked)))
                }
            }
        }
    }

    private fun hideProgress() {
        _viewState.setValue(MyEventViewState.HideProgress)
    }

    fun onFabButtonClicked() {
        _viewState.setValue(MyEventViewState.NavigateToAddEvent)
    }

    fun onDeleteEvent(event: Event) {
        eventRepository.deleteEvent(event.eventId) { request ->
            when (request) {
                is ResponseState.Failure -> {
                    _viewState.setValue(MyEventViewState.ShowError(DELETE_EVENT_REQUEST_FAILED))
                }
                is ResponseState.Success -> {
                    deletedEvent = event
                    _viewState.setValue(MyEventViewState.UpdateEvent(event))
                }
            }
        }
    }

    fun onUndoDeleteEvent() {
        deletedEvent?.let { event ->
            _viewState.setValue(MyEventViewState.UpdateEvent(event))
            deletedEvent = null
        }
    }

    fun onEditEvent(it: Event) {
        // this is just for sake of being able to see something when swiped right to edit.
        _viewState.setValue(MyEventViewState.NavigateToEditMYEvent)
    }

    companion object {
        const val LIKE_EVENT_REQUEST_FAILED = "LIKE_EVENT_REQUEST_FAILED"
        const val DELETE_EVENT_REQUEST_FAILED = "DELETE_EVENT_REQUEST_FAILED"
    }
}