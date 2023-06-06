package com.gathering.android.event.home


import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.event.home.model.EventsRepository
import com.gathering.android.event.home.view.Filter
import com.gathering.android.event.home.view.SortType
import com.gathering.android.event.home.viewmodel.EventViewState
import com.gathering.android.event.model.Event
import javax.inject.Inject

class EventListViewModel @Inject constructor(
    private val eventRepository: EventsRepository,
    private val eventLocationComparator: EventLocationComparator
) :
    ViewModel() {

    private val _viewState = ActiveMutableLiveData<EventViewState>()
    val viewState: LiveData<EventViewState> by ::_viewState

    private var lastSortType = SortType.SORT_BY_DATE
    private var lastFilter = Filter()
    fun onViewCreated() {
        loadEventList(lastFilter, lastSortType)
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
        val filteredEventList = getRefinedEventList(
            filter, sortType
        )
        _viewState.setValue(EventViewState.ShowEventList(filteredEventList))

        if (filteredEventList.isEmpty()) {
            _viewState.setValue(EventViewState.ShowNoData)
        } else {
            _viewState.setValue(EventViewState.HideNoData)
        }

        _viewState.setValue(EventViewState.HideProgress)
    }

    fun onEventItemClicked(event: Event) {
        _viewState.setValue(EventViewState.NavigateToEventDetail(event))
    }

    private fun getRefinedEventList(
        filter: Filter, sortType: SortType
    ): List<Event> {
        val eventList = eventRepository.provideEventListMock()
        var filteredEventList: List<Event> = eventList

        if (filter.isContactsFilterOn) {
            filteredEventList = filteredEventList.filter { event ->
                event.isContactEvent
            }
        }

        if (filter.isMyEventsFilterOn) {
            filteredEventList = filteredEventList.filter { event ->
                event.isMyEvent
            }
        }

        if (filter.isTodayFilterOn) {
            filteredEventList = filteredEventList.filter { event ->
                DateUtils.isToday(event.dateAndTime)

            }
        }

        filteredEventList = if (sortType == SortType.SORT_BY_DATE) {
            filteredEventList.sortedBy { it.dateAndTime }
        } else {
            filteredEventList.sortedWith(eventLocationComparator)
        }
        return filteredEventList
    }
}


