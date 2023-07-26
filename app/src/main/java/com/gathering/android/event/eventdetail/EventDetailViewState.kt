package com.gathering.android.event.eventdetail

import com.gathering.android.event.Event

sealed interface EventDetailViewState {

    class ShowEventDetail(val event: Event) : EventDetailViewState

    class ShowError(val errorMessage: String) : EventDetailViewState

    object NavigateToAttendeesDetailBottomSheet : EventDetailViewState

    object YesSelected : EventDetailViewState

    object NoSelected : EventDetailViewState

    object MaybeSelected : EventDetailViewState
}

