package com.gathering.android.event.home


import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.event.home.model.Event
import java.util.*
import javax.inject.Inject

class EventListViewModel @Inject constructor() : ViewModel() {

    private val _viewState = ActiveMutableLiveData<EventViewState>()
    val viewState: LiveData<EventViewState> by ::_viewState

    fun onViewCreated() {
        loadEventList()
    }

    fun onFilterChanged(filter: Filter) {
        loadEventList(filter)
    }

    private fun loadEventList(filter: Filter = Filter()) {
        _viewState.setValue(EventViewState.ShowProgress)
        val filteredEventList = getRefinedEventList(
            filter
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
        filter: Filter
    ): List<Event> {
        val eventList = provideEventListMock()
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
                DateUtils.isToday(event.date.time)

            }
        }
        return filteredEventList
    }

    private fun provideEventListMock(): List<Event> {
        return mutableListOf(

            Event(
                eventId = "1",
                eventName = "anahid's Party",
                hostName = "Anahid",
                description = "Dinner and game is waiting for you",
                photoUrl = "",
                location = "Anahid's Home",
                startTime = "7:00",
                endTime = "midnight",
                date = Calendar.getInstance().time,
                isContactEvent = false,
                activities = listOf("Game", "Dinner", "Tea Time"),
                eventCost = 10
            ),
            Event(
                eventId = "2",
                eventName = "Ida's Party",
                hostName = "Ida",
                description = "Dinner and game and swimming pool is waiting for you",
                photoUrl = "",
                location = "Ida's Home",
                startTime = "8:00",
                endTime = "12:00",
                date = Calendar.getInstance().time,
                isContactEvent = true,
                isMyEvent = true,
                activities = listOf("Game", "Dinner", "Tea Time"),
                eventCost = 10
            ),
            Event(
                eventId = "3",
                eventName = "Amir's Party",
                hostName = "Amir",
                description = "Dinner and game and swimming pool is waiting for you",
                photoUrl = "",
                location = "Amir's Home",
                startTime = "8:00",
                endTime = "12:00",
                date = Calendar.getInstance().time,
                isContactEvent = true,
                activities = listOf("Game", "Dinner", "Tea Time"),
                eventCost = 10
            ),
            Event(
                eventId = "4",
                eventName = "Mo's Party",
                hostName = "Mo",
                description = "Dinner and game and swimming pool is waiting for you",
                photoUrl = "",
                location = "Mo's Home",
                startTime = "8:00",
                endTime = "12:00",
                date = Calendar.getInstance().run {
                    add(Calendar.DAY_OF_YEAR, 1)
                    time
                },
                isContactEvent = true,
                activities = listOf("Game", "Dinner", "Tea Time"),
                eventCost = 10
            )
        )
    }
}


