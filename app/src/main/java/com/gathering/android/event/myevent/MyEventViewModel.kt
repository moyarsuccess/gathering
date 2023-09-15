package com.gathering.android.event.myevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.ResponseState
import com.gathering.android.event.Event
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.repo.EventRepository
import com.gathering.android.event.toEvent
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MyEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private var deletedEvent: Event? = null
    private var deletedEventIndex: Int = 0
    private var page = 1
    private var myEventNavigator: MyEventNavigator? = null

    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UiState()
    )

    data class UiState(
        val showNoData: Boolean = false,
        val showProgress: Boolean = false,
        val myEvents: List<Event> = emptyList(),
        var errorMessage: String? = null,
    )

    fun onViewCreated(myEventNavigator: MyEventNavigator) {
        this.myEventNavigator = myEventNavigator
        getMyEvents(page)
    }

    fun onEventAdded() {
        viewModelState.update { currentViewState ->
            currentViewState.copy(myEvents = emptyList())
        }
        page = 1
        getMyEvents(page)
    }

    private fun getMyEvents(page: Int) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(showProgress = true)
        }
        eventRepository.getMyEvents(page) { request ->
            when (request) {
                is ResponseState.Failure -> {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(
                            showProgress = false, errorMessage = MY_EVENTS_REQUEST_FAILED
                        )
                    }
                }
                is ResponseState.Success<List<EventModel>> -> {
                    val currentPageEvents = request.data as? List<EventModel>
                    if (currentPageEvents.isNullOrEmpty()) {
                        if (page == 1) {
                            viewModelState.update { currentViewState ->
                                currentViewState.copy(showNoData = true)
                            }
                        }
                    } else {
                        viewModelState.update { currentViewState ->
                            currentViewState.copy(
                                myEvents = currentViewState.myEvents.plus(currentPageEvents.map { it.toEvent() })
                            )
                        }
                    }
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(showProgress = false)
                    }
                }
            }
        }
    }

    fun onNextPageRequested() {
        page++
        getMyEvents(page)
    }

    fun onSwipedToDelete(event: Event) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(showProgress = true)
        }
        eventRepository.deleteEvent(event.eventId) { request ->
            when (request) {
                is ResponseState.Failure -> {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(
                            showProgress = false, errorMessage = DELETE_EVENT_REQUEST_FAILED
                        )
                    }
                }
                is ResponseState.Success -> {
                    deletedEvent = event
                    deletedEventIndex =
                        viewModelState.value.myEvents.indexOfFirst { it.eventId == event.eventId }
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(showProgress = false,
                            myEvents = currentViewState.myEvents.toMutableList()
                                .apply { this.removeAt(deletedEventIndex) })
                    }
                }
            }
        }
    }
    fun onUndoDeleteEvent() {
        deletedEvent?.let { deletedElement ->
            val mutableEventList = viewModelState.value.myEvents.toMutableList().apply {
                this.add(
                    deletedEventIndex, deletedElement
                )
            }

            viewModelState.update { currentViewState -> currentViewState.copy(myEvents = mutableEventList) }
        }
        deletedEvent = null
        deletedEventIndex = 0
    }

    fun onEditEventClicked(event: Event) {
        myEventNavigator?.navigateToEditEvent(event)
    }

    fun onEventLikeClicked(event: Event) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(showProgress = true)
        }
        val liked = !event.liked
        val eventId = event.eventId
        eventRepository.likeEvent(eventId, liked) { request ->
            when (request) {
                is ResponseState.Failure -> {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(
                            showProgress = false, errorMessage = LIKE_EVENT_REQUEST_FAILED
                        )
                    }
                }
                is ResponseState.Success -> {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(
                            showProgress = false,
                            myEvents = currentViewState.myEvents.apply {
                                val index = this.indexOfFirst { it.eventId == eventId }
                                this.toMutableList()[index] = event.copy(liked = liked)
                            })
                    }
                }
            }
        }
    }

    fun onFabButtonClicked() {
        myEventNavigator?.navigateToAddEvent()
    }

    companion object {
        const val DELETE_EVENT_REQUEST_FAILED = "DELETE_EVENT_REQUEST_FAILED"
        const val LIKE_EVENT_REQUEST_FAILED = "LIKE_EVENT_REQUEST_FAILED"
        const val MY_EVENTS_REQUEST_FAILED = "COULD NOT GET MY EVENTS LIST"
    }
}