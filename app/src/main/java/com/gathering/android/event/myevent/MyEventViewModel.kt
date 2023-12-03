package com.gathering.android.event.myevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.toImageUrl
import com.gathering.android.event.DELETE_EVENT_REQUEST_FAILED
import com.gathering.android.event.Event
import com.gathering.android.event.General_ERROR
import com.gathering.android.event.LIKE_EVENT_REQUEST_FAILED
import com.gathering.android.event.SERVER_NOT_RESPONDING_TO_SHOW_MY_EVENT
import com.gathering.android.event.repo.EventException
import com.gathering.android.event.repo.EventRepository
import com.gathering.android.event.toEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    private var deletedEvent: Event? = null
    private var deletedEventIndex: Int = 0
    private var page = 1
    private var myEventNavigator: MyEventNavigator? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is EventException -> {
                when (throwable) {
                    EventException.LikeEventServerRequestFailedException -> LIKE_EVENT_REQUEST_FAILED
                    EventException.ServerNotRespondingException -> SERVER_NOT_RESPONDING_TO_SHOW_MY_EVENT
                    EventException.DeleteEventServerRequestFailedException -> DELETE_EVENT_REQUEST_FAILED
                    else -> {
                        General_ERROR
                    }
                }
            }

            else -> {
                General_ERROR
            }
        }
        viewModelState.update { currentState ->
            currentState.copy(
                errorMessage = errorMessage,
                showProgress = false,
                showNoData = currentState.myEvents.isEmpty()
            )
        }
    }

    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.map {
        it.copy(
            myEvents = it.myEvents.map { event ->
                event.copy(photoUrl = event.photoUrl.toImageUrl())
            }
        )
    }.stateIn(
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
        page = 1
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
        viewModelScope.launch(exceptionHandler) {
            val myEvents = eventRepository.getMyEvents(page)
            viewModelState.update { currentState ->
                currentState.copy(
                    showNoData = false,
                    showProgress = false,
                    myEvents = currentState.myEvents.plus(myEvents.map { it.toEvent() })
                )
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
        viewModelScope.launch(exceptionHandler) {
            eventRepository.deleteEvent(event.eventId)
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

    fun onUndoDeleteEvent(event: Event) {
        val deletedEventIndex = viewModelState.value.myEvents.indexOf(event)
        if (deletedEventIndex != -1) {
            val mutableEventList = viewModelState.value.myEvents.toMutableList()
            mutableEventList.add(deletedEventIndex, event)

            viewModelState.update { currentViewState ->
                currentViewState.copy(myEvents = mutableEventList)
            }
        }
    }

    fun onEditEventClicked(event: Event) {
        myEventNavigator?.navigateToEditEvent(event)
    }

    fun onEventItemClicked(eventId: Long) {
        myEventNavigator?.navigateToConfirmedAttendeesScreen(eventId)
    }

    fun onEventLikeClicked(event: Event) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(showProgress = true)
        }

        val liked = !event.liked
        val eventId = event.eventId

        viewModelScope.launch(exceptionHandler) {
            eventRepository.likeEvent(eventId, liked)
        }

        viewModelState.update { currentViewState ->
            currentViewState.copy(
                showProgress = false,
                myEvents = currentViewState.myEvents.apply {
                    val index = this.indexOfFirst { it.eventId == eventId }
                    this.toMutableList()[index] = event.copy(liked = liked)
                })
        }
    }

    fun onFabButtonClicked() {
        myEventNavigator?.navigateToAddEvent()
    }
}