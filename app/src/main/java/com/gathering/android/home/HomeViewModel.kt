package com.gathering.android.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.common.ResponseState
import com.gathering.android.common.toImageUrl
import com.gathering.android.event.Event
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

    private var homeNavigator: HomeNavigator? = null
    private var page = 1

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is EventException -> {
                when (throwable) {
                    EventException.ServerNotRespondingException -> SERVER_NOT_RESPONDING_TO_SHOW_EVENTS
                    EventException.LikeEventServerRequestFailedException -> LIKE_EVENT_REQUEST_FAILED
                    is EventException.GeneralException -> General_ERROR
                }
            }

            else -> {
                General_ERROR
            }
        }
        viewModelState.update { currentState ->
            currentState.copy(errorMessage = errorMessage)
        }
    }

    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.map {
        it.copy(
            events = it.events.map { event ->
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

    private fun getEvents(page: Int) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(showProgress = true)
        }

        viewModelScope.launch {
            val events = try {
                eventRepository.getEvents(page)
            } catch (e: Exception) {
                viewModelState.update { currentState ->
                    currentState.copy(
                        showProgress = false,
                        showNoData = true,
                        errorMessage = SERVER_ERROR,
                    )
                }
                return@launch
            }
            viewModelState.update { currentViewState ->
                currentViewState.copy(
                    showNoData = false,
                    showProgress = false,
                    events = (currentViewState.events + events.map { it.toEvent() }).distinct()
                )
            }
        }
    }

    fun onNextPageRequested() {
        page++
        getEvents(page)
    }

    fun onEventItemClicked(eventId: Long) {
        homeNavigator?.navigateToEventDetail(eventId)
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
    }

    companion object {
        private const val LIKE_EVENT_REQUEST_FAILED = "You are not able to like this event"
        private const val SERVER_NOT_RESPONDING_TO_SHOW_EVENTS =
            "The event list is not available now :("
        private const val General_ERROR = "Oops! something wrong"
        private const val SERVER_ERROR = "The event list is not available now :("
    }
}
