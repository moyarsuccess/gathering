package com.gathering.android.event.myevent

import com.gathering.android.event.Event

sealed interface MyEventViewState {

    class ShowUserEventList(val myEventList: List<Event>) : MyEventViewState

    object NavigateToAddEvent : MyEventViewState
    class UpdateEvent(val event: Event) : MyEventViewState

    class ShowError(val errorMessage: String) : MyEventViewState

    object ShowProgress : MyEventViewState

    object HideProgress : MyEventViewState

    object ShowNoData : MyEventViewState

    object HideNoData : MyEventViewState

    class NavigateToEditMyEvent(val event: Event) : MyEventViewState

}

