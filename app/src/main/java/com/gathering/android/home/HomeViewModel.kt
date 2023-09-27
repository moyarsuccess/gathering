package com.gathering.android.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.auth.verification.repo.VerificationRepository
import com.gathering.android.common.ResponseState
import com.gathering.android.event.Event
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.repo.EventRepository
import com.gathering.android.event.toEvent
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val verificationRepository: VerificationRepository,
    private val eventRepository: EventRepository,
    private val eventLocationComparator: EventLocationComparator,
    private val eventDateComparator: EventDateComparator
) : ViewModel() {


    //    private var lastSortType = SortType.SORT_BY_DATE
    //    private var lastFilter = Filter()
    private var homeNavigator: HomeNavigator? = null
    private var page = 1

    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState.stateIn(
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
        if (!verificationRepository.isUserVerified()) {
            homeNavigator.navigateToIntroScreen()
        } else {
            page = 1
            getEvents(page)
        }
    }

    private fun getEvents(page: Int) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(showProgress = true)
        }
        eventRepository.getEvents(page) { request ->
            when (request) {
                is ResponseState.Failure -> {
                    viewModelState.update { currentViewState ->
                        currentViewState.copy(
                            showProgress = false,
                            showNoData = true,
                            errorMessage = EVENTS_REQUEST_FAILED
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
                                events = currentViewState.events.plus(currentPageEvents.map { it.toEvent() })
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
        getEvents(page)
    }

//    private fun List<EventModel>.applySortAndFilter(): List<EventModel> {
//        return filter { event ->
//            if (!lastFilter.isContactsFilterOn) true
//            else !event.isMyEvent
//        }.filter { event ->
//            if (!lastFilter.isMyEventsFilterOn) true
//            else event.isMyEvent
//        }.filter { event ->
//            if (!lastFilter.isTodayFilterOn) true
//            else DateUtils.isToday(event.dateTime ?: 0)
//        }.sortedWith(lastSortType.getProperComparator())
//    }

//    fun onSortChanged(sortType: SortType) {
////        loadEventList(sortType = sortType, filter = lastFilter)
//        lastSortType = sortType
//    }

//    fun onFilterChanged(filter: Filter) {
////        loadEventList(filter = filter, lastSortType)
//        lastFilter = filter
//    }

    fun onEventItemClicked(event: Event) {
        homeNavigator?.navigateToEventDetail(event)
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
                        currentViewState.copy(showProgress = false,
                            events = currentViewState.events.apply {
                                val index = this.indexOfFirst { it.eventId == eventId }
                                this.toMutableList()[index] = event.copy(liked = liked)
                            })
                    }
                }
            }
        }
    }

//    private fun SortType.getProperComparator(): Comparator<EventModel> {
//        return when (this) {
//            SortType.SORT_BY_LOCATION -> eventLocationComparator
//            SortType.SORT_BY_DATE -> eventDateComparator
//        }
//    }

    companion object {
        private const val LIKE_EVENT_REQUEST_FAILED = "LIKE_EVENT_REQUEST_FAILED"
        private const val EVENTS_REQUEST_FAILED = "FAILED TO LOAD EVENTS."
    }
}

