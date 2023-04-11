package com.gathering.android.event.home

import androidx.lifecycle.LiveData

import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.event.home.model.Event
import javax.inject.Inject

class EventListViewModel @Inject constructor() : ViewModel() {

    private val _viewState = ActiveMutableLiveData<EventViewState>()
    val viewState: LiveData<EventViewState> by ::_viewState

    fun onViewCreated() {
        _viewState.setValue(EventViewState.ShowProgress)
        val list = provideEventList()
        if (list.isNotEmpty()) {
            _viewState.setValue(EventViewState.ShowEventList(list))
            _viewState.setValue(EventViewState.HideNoData)
        } else {
            _viewState.setValue(EventViewState.ShowNoData)
        }
        _viewState.setValue(EventViewState.HideProgress)

    }

    fun onEventItemClicked(event: Event) {
        EventViewState.NavigateToEventDetail(event)
    }

    private fun provideEventList(): List<Event> {
        val eventList = mutableListOf<Event>()
        eventList.add(
            Event(
                eventId = "1",
                eventName = "Anahid's Party",
                hostName = "Anahid",
                description = "Dinner and game is waiting for you",
                photoUrl = "",
                location = "Anahid's Home",
                startTime = "7:00",
                endTime = "midnight",
                date = "20/12/2023",
                activities = listOf("Game", "Dinner", "Tea Time"),
                eventCost = 10
            )
        )
        eventList.add(
            Event(
                eventId = "2",
                eventName = "Ida's Party",
                hostName = "Ida",
                description = "Dinner and game and swimming pool is waiting for you",
                photoUrl = "anahid",
                location = "Ida's Home",
                startTime = "8:00",
                endTime = "12:00",
                date = "25/12/2023",
                activities = listOf("Game", "Dinner", "Tea Time"),
                eventCost = 10
            )
        )
        return eventList
    }
}
