package com.gathering.android.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.auth.verification.repo.VerificationRepository
import com.gathering.android.common.ResponseState
import com.gathering.android.common.toImageUrl
import com.gathering.android.event.Event
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.repo.EventRepository
import com.gathering.android.common.exception.ResponseWasNull
import com.gathering.android.event.toEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class HomeViewModel @Inject constructor(
    private val verificationRepository: VerificationRepository,
    private val eventRepository: EventRepository,
) : ViewModel() {

    private var homeNavigator: HomeNavigator? = null
    private var page = 1

    private val exceptionHandler = object : CoroutineExceptionHandler {
        override val key: CoroutineContext.Key<*>
            get() = TODO("Not yet implemented")

        override fun handleException(context: CoroutineContext, exception: Throwable) {
            when (exception) {
                is ResponseWasNull -> viewModelState.update { currentState ->
                    currentState.copy(errorMessage = exception.message)
                }
                else -> {
                    viewModelState.update { currentState ->
                        currentState.copy(errorMessage = exception.message)
                    }
                }
            }
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
        if (verificationRepository.isUserVerified()) {
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
            val events = eventRepository.getEvents(page, exceptionHandler)
            if (events.isEmpty()) {
                if (page == 1) {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(showNoData = true)
                    }
                }
            } else {
                viewModelState.update { currentViewState ->
                    currentViewState.copy(
                        events = currentViewState.events + events.map { it.toEvent() }
                    )
                }
            }
            viewModelState.update { currentViewState ->
                currentViewState.copy(showProgress = false)
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
        private const val LIKE_EVENT_REQUEST_FAILED = "LIKE_EVENT_REQUEST_FAILED"
        private const val EVENTS_REQUEST_FAILED = "FAILED TO LOAD EVENTS."
    }
}
