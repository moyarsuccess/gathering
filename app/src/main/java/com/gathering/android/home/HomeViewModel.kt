package com.gathering.android.home

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.event.Event
import com.gathering.android.event.GENERAL_ERROR
import com.gathering.android.event.LIKE_EVENT_REQUEST_FAILED
import com.gathering.android.event.SERVER_NOT_RESPONDING_TO_SHOW_EVENTS
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

class HomeViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val eventRepository: EventRepository,
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var homeNavigator: HomeNavigator? = null
    private var page = 1

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is EventException -> {
                when (throwable) {
                    EventException.ServerNotRespondingException -> SERVER_NOT_RESPONDING_TO_SHOW_EVENTS
                    EventException.LikeEventServerRequestFailedException -> LIKE_EVENT_REQUEST_FAILED
                    is EventException.GeneralException -> GENERAL_ERROR
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
                showNoData = currentState.events.isEmpty(),
            )
        }
    }

    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.map {
        UiState(
            showNoData = it.showNoData,
            showProgress = it.showProgress,
            events = it.events,
            errorMessage = it.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UiState()
    )

    data class UiState(
        val showNoData: Boolean = false,
        val showProgress: Boolean = false,
        val events: List<Event> = emptyList(),
        var errorMessage: String? = null,
    )

    fun onViewCreated(homeNavigator: HomeNavigator) {
        this.homeNavigator = homeNavigator
        if (repository.isUserVerified()) {
            page = 1
            getEvents(page)
        } else {
            homeNavigator.navigateToIntroScreen()
        }
    }

    fun onNextPageRequested() {
        page++
        getEvents(page)
    }

    private fun getEvents(page: Int) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(showProgress = true)
        }
        viewModelScope.launch(exceptionHandler) {
            val events = eventRepository.getEvents(page)
            viewModelState.update { currentViewState ->
                currentViewState.copy(
                    showNoData = false,
                    showProgress = false,
                    events = (currentViewState.events + events.map { it.toEvent() }).distinct()
                )
            }

        }
    }

    fun onEventItemClicked(eventId: Long) {
        homeNavigator?.navigateToEventDetail(eventId)
    }

    fun onEventLikeClicked(event: Event) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(showProgress = true)
        }
        viewModelScope.launch(exceptionHandler) {
            val liked = !event.liked
            val eventId = event.eventId

            eventRepository.likeEvent(eventId, liked)

            viewModelState.update { currentViewState ->
                val list = currentViewState.events.toMutableList()
                val index = list.indexOfFirst { it.eventId == eventId }
                val newEvent = list[index].copy(liked = liked)
                list[index] = newEvent
                currentViewState.copy(
                    showProgress = false,
                    events = list
                )
            }
        }
    }
}
