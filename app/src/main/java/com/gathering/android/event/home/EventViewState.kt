package com.gathering.android.event.home

import com.gathering.android.event.Event

sealed interface EventViewState {

    class ShowEventList(val eventList: List<Event>) : EventViewState

    class AppendEventList(val eventList: List<Event>) : EventViewState

    class NavigateToEventDetail(val event: Event) : EventViewState

    class ShowError(val errorMessage: String) : EventViewState

    class UpdateEvent(val event: Event) : EventViewState

    object ShowProgress : EventViewState

    object HideProgress : EventViewState

    object ShowNoData : EventViewState

    object HideNoData : EventViewState

    object NavigateToIntroScreen : EventViewState

}
