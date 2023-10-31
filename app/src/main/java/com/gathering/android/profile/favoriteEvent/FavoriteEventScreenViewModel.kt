package com.gathering.android.profile.favoriteEvent


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.ResponseState
import com.gathering.android.event.Event
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.repo.EventRepository
import com.gathering.android.event.toEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class FavoriteEventScreenViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private var favoriteEventNavigator: FavoriteEventScreen? = null
    private var page = 1

    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = UiState()
    )

    data class UiState(
        val favoriteEvents: List<Event> = emptyList(),
        val showNoData: Boolean = false,
        val errorMessage: String = ""
    )

    fun onViewCreated(favoriteEventNavigator: FavoriteEventScreen) {
        this.favoriteEventNavigator = favoriteEventNavigator
        page = 1
        loadFavoriteEvents(page)
    }

    private fun loadFavoriteEvents(page: Int) {
        eventRepository.likedMyEvents(page) { request ->
            when (request) {
                is ResponseState.Failure -> {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(errorMessage = FAILED_TO_LOAD_FAVORITE_EVENTS)
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
                        val likedEvents = currentPageEvents.filter { it.liked }
                        viewModelState.update { currentViewState ->
                            currentViewState.copy(favoriteEvents = currentViewState.favoriteEvents
                                .plus(likedEvents.map { it.toEvent() }
                                )
                            )
                        }

                    }
                }
            }
        }
    }
    fun onEventItemClicked(event: Event) {
        favoriteEventNavigator?.navigateToEventDetail(event)
    }

    companion object {
        private const val FAILED_TO_LOAD_FAVORITE_EVENTS = "FAILED TO LOAD FAVORITE EVENTS."
    }
}







