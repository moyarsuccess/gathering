package com.gathering.android.event.myevent

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.ResponseState
import com.gathering.android.event.Event
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.repo.EventRepository
import com.gathering.android.event.toEvent
import javax.inject.Inject

class MyEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private var page = 1
    private val _viewState = ActiveMutableLiveData<MyEventViewState>()
    val viewState: MutableLiveData<MyEventViewState> by ::_viewState

    private var deletedEvent: Event? = null
    fun onViewCreated() {
        getMyEvents(page)
    }

    fun onEventAdded() {
        _viewState.setValue(MyEventViewState.ClearData)
        page = 1
        getMyEvents(page)
    }

    private fun getMyEvents(page: Int) {
        _viewState.setValue(MyEventViewState.ShowProgress)
        eventRepository.getMyEvents(page) { request ->
            when (request) {
                is ResponseState.Failure -> {
                    _viewState.setValue(MyEventViewState.HideProgress)
                    _viewState.setValue(MyEventViewState.ShowNoData)
                }

                is ResponseState.Success<List<EventModel>> -> {
                    val currentPageEvents = request.data as? List<EventModel>
                    if (currentPageEvents.isNullOrEmpty()) {
                        if (page == 0) {
                            _viewState.setValue(MyEventViewState.ShowNoData)
                        }
                        hideProgress()
                    } else {
                        _viewState.setValue(MyEventViewState.HideProgress)
                        _viewState.setValue(MyEventViewState.ShowNextEventPage(currentPageEvents.map { it.toEvent() }))
                    }

                }
            }
        }
    }

    fun onNextPageRequested() {
        page++
        getMyEvents(page)
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

    fun onEditEventClicked(event: Event) {
        _viewState.setValue(MyEventViewState.NavigateToEditMyEvent(event))
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

    companion object {
        const val DELETE_EVENT_REQUEST_FAILED = "DELETE_EVENT_REQUEST_FAILED"
        const val LIKE_EVENT_REQUEST_FAILED = "LIKE_EVENT_REQUEST_FAILED"
    }
}