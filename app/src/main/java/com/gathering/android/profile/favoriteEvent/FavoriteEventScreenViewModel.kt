package com.gathering.android.profile.favoriteEvent


import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.toImageUrl
import com.gathering.android.event.Event
import com.gathering.android.event.GENERAL_ERROR
import com.gathering.android.event.SERVER_NOT_RESPONDING_TO_SHOW_MY_FAVORITE_EVENT
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

class FavoriteEventScreenViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var favoriteEventNavigator: FavoriteEventNavigator? = null

    private var page = 1

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is EventException -> {
                when (throwable) {
                    EventException.ServerNotRespondingException -> SERVER_NOT_RESPONDING_TO_SHOW_MY_FAVORITE_EVENT
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
                showNoData = currentState.favoriteEvents.isEmpty(),
            )
        }
    }


    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.map {
        it.copy(
            favoriteEvents = it.favoriteEvents.map { event ->
                event.copy(photoUrl = event.photoUrl.toImageUrl())
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UiState()
    )

    data class UiState(
        val favoriteEvents: List<Event> = emptyList(),
        val showNoData: Boolean = false,
        val errorMessage: String? = null,
        val showProgress: Boolean = false,
    )

    fun onViewCreated(favoriteEventNavigator: FavoriteEventNavigator) {
        this.favoriteEventNavigator = favoriteEventNavigator
        page = 1
        loadFavoriteEvents(page)
    }

    private fun loadFavoriteEvents(page: Int) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(showProgress = true)
        }

        viewModelScope.launch(exceptionHandler) {
            val likedEvents = eventRepository.getMyLikedEvents(page)

            if (likedEvents.isNotEmpty()) {
                viewModelState.update { currentViewState ->
                    currentViewState.copy(
                        favoriteEvents = currentViewState.favoriteEvents.plus(likedEvents.map { it.toEvent() }),
                        showNoData = false,
                        showProgress = false
                    )
                }
            }
        }
    }

    fun onNextPageRequested() {
        page++
        loadFavoriteEvents(page)
    }

    fun onEventItemClicked(event: Event) {
        favoriteEventNavigator?.navigateToEventDetail(event)
    }
}







