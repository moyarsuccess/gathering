package com.gathering.android.event.home


import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.auth.AuthRepository
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.event.home.view.Filter
import com.gathering.android.event.home.view.SortType
import com.gathering.android.event.home.viewmodel.EventViewState
import com.gathering.android.event.model.Event
import com.gathering.android.event.model.EventRepository
import com.gathering.android.common.ResponseState
import javax.inject.Inject

class EventListViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val eventRepository: EventRepository,
    private val eventLocationComparator: EventLocationComparator,
    private val eventDateComparator: EventDateComparator
) :
    ViewModel() {

    private val _viewState = ActiveMutableLiveData<EventViewState>()
    val viewState: LiveData<EventViewState> by ::_viewState

    private var lastSortType = SortType.SORT_BY_DATE
    private var lastFilter = Filter()
    fun onViewCreated() {
        if (!authRepository.isSignedIn() || !authRepository.isUserVerified()) {
            _viewState.setValue(EventViewState.NavigateToIntroScreen)
        } else {
            loadEventList(lastFilter, lastSortType)
        }
    }

    fun onSortChanged(sortType: SortType) {
        loadEventList(sortType = sortType, filter = lastFilter)
        lastSortType = sortType
    }

    fun onFilterChanged(filter: Filter) {
        loadEventList(filter = filter, lastSortType)
        lastFilter = filter
    }

    private fun loadEventList(
        filter: Filter,
        sortType: SortType
    ) {
        _viewState.setValue(EventViewState.ShowProgress)
        getRefinedEventList(
            filter, sortType
        ) {
            _viewState.setValue(EventViewState.ShowEventList(it))

            if (it.isEmpty()) {
                _viewState.setValue(EventViewState.ShowNoData)
            } else {
                _viewState.setValue(EventViewState.HideNoData)
            }

            _viewState.setValue(EventViewState.HideProgress)
        }
    }

    fun onEventItemClicked(event: Event) {
        _viewState.setValue(EventViewState.NavigateToEventDetail(event))
    }

    @Suppress("UNCHECK_CAST")
    private fun getRefinedEventList(
        filter: Filter,
        sortType: SortType,
        onFilteredEventsReady: (eventList: List<Event>) -> Unit
    ) {
        eventRepository.getAllEvents { request ->
            when (request) {
                is ResponseState.Failure -> hideProgress()
                is ResponseState.Success<*> -> {
                    (request.data as? List<Event>)?.also { eventList ->
                        val filteredList = eventList
                            .filter { event ->
                                if (!filter.isContactsFilterOn) true
                                else event.isContactEvent
                            }.filter { event ->
                                if (!filter.isMyEventsFilterOn) true
                                else event.isMyEvent
                            }.filter { event ->
                                if (!filter.isTodayFilterOn) true
                                else DateUtils.isToday(event.dateAndTime)
                            }.sortedWith(sortType.getProperComparator())

                        onFilteredEventsReady(filteredList)
                    }
                }
            }
        }
    }

    private fun SortType.getProperComparator(): Comparator<Event> {
        return when (this) {
            SortType.SORT_BY_LOCATION -> eventLocationComparator
            SortType.SORT_BY_DATE -> eventDateComparator
        }
    }

    private fun hideProgress() {
        _viewState.setValue(EventViewState.HideProgress)
    }
}


