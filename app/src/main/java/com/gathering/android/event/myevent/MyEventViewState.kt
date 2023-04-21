package com.gathering.android.event.myevent

sealed interface MyEventViewState {

    object ShowUserEventList : MyEventViewState

    object NavigateToAddEvent : MyEventViewState

    class ShowError(val errorMessage: String) : MyEventViewState

    object ShowProgress : MyEventViewState

    object HideProgress : MyEventViewState

    object ShowNoData : MyEventViewState

    object HideNoData : MyEventViewState
}

