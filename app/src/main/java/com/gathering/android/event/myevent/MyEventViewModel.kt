package com.gathering.android.event.myevent

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.event.DELETE_EVENT_REQUEST_FAILED
import com.gathering.android.event.Event
import com.gathering.android.event.GENERAL_ERROR
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

    private var page = 1

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var myEventNavigator: MyEventNavigator? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is EventException -> {
                when (throwable) {
                    EventException.LikeEventServerRequestFailedException -> LIKE_EVENT_REQUEST_FAILED
                    EventException.ServerNotRespondingException -> SERVER_NOT_RESPONDING_TO_SHOW_MY_EVENT
                    EventException.DeleteEventServerRequestFailedException -> DELETE_EVENT_REQUEST_FAILED
                    else -> {
                        GENERAL_ERROR
                    }
                }
            }

            else -> {
                GENERAL_ERROR
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

    private val viewModelState = MutableStateFlow(ViewModelState())
    val uiState: StateFlow<UiState> = viewModelState.map {
        UiState(
            showNoData = it.showNoData,
            showProgress = it.showProgress,
            showSnackBar = it.showSnackBar,
            myEvents = it.myEvents,
            errorMessage = it.errorMessage,
            deletedEventName = it.deletedEvent?.eventName,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UiState()
    )

    data class UiState(
        val showNoData: Boolean = false,
        val showProgress: Boolean = false,
        val showSnackBar: Boolean = false,
        val myEvents: List<Event> = emptyList(),
        val errorMessage: String? = null,
        val deletedEventName: String? = null,
    )

    data class ViewModelState(
        val showNoData: Boolean = false,
        val showProgress: Boolean = false,
        val showSnackBar: Boolean = false,
        val myEvents: List<Event> = emptyList(),
        val errorMessage: String? = null,
        var deletedEvent: Event? = null,
        val deletedEventPosition: Int = -1
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
            val list = currentViewState
                .myEvents
                .toMutableList()
            val deletedIndex = list.indexOfFirst { it.eventId == event.eventId }
            list.removeAt(deletedIndex)
            currentViewState.copy(
                showSnackBar = true,
                deletedEvent = event,
                deletedEventPosition = deletedIndex,
                myEvents = list
            )
        }
    }

    fun onUndoClicked() {
        undoDeletedEvent()
    }

    private fun undoDeletedEvent() {
        viewModelState.update { currentViewState ->
            val deletedEvent = currentViewState.deletedEvent ?: return
            val index = if (currentViewState.deletedEventPosition != -1) {
                currentViewState.deletedEventPosition
            } else return
            val list = currentViewState
                .myEvents
                .toMutableList()
            list.add(index, deletedEvent)
            currentViewState.copy(
                showSnackBar = false,
                deletedEvent = null,
                deletedEventPosition = -1,
                myEvents = list,
            )
        }
    }

    fun onSnackBarDismissed() {
        viewModelScope.launch(exceptionHandler) {
            try {
                val deletedEvent = viewModelState.value.deletedEvent ?: return@launch
                eventRepository.deleteEvent(deletedEvent.eventId)
                viewModelState.update { currentViewState ->
                    currentViewState.copy(
                        showProgress = false,
                        deletedEvent = null,
                        deletedEventPosition = -1,
                    )
                }
            } catch (e: Exception) {
                undoDeletedEvent()
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