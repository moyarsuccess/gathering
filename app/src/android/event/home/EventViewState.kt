package com.gathering.android.event.home

import com.gathering.android.event.home.model.Event

sealed interface EventViewState {

    class ShowEventList(val eventList: List<Event>) : EventViewState

    class NavigateToEventDetail(val event: Event) : EventViewState

    class ShowError(val errorMessage: String) : EventViewState

    object ShowProgress : EventViewState

    object HideProgress : EventViewState

    object ShowNoData : EventViewState

    object HideNoData : EventViewState
}
