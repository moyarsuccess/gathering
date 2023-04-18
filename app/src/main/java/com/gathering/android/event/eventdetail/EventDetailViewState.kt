package com.gathering.android.event.eventdetail

import com.gathering.android.event.home.model.Event

sealed interface EventDetailViewState {

    class ShowEventDetail(val event: Event) : EventDetailViewState

    class ShowError(val errorMessage: String) : EventDetailViewState
}

