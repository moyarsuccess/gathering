package com.gathering.android.event.eventdetail

import com.gathering.android.event.Event
import com.gathering.android.event.model.Attendee

sealed interface EventDetailViewState {

    class ShowEventDetail(val event: Event) : EventDetailViewState

    class ShowError(val errorMessage: String) : EventDetailViewState

    class NavigateToAttendeesDetailBottomSheet(val attendees: List<Attendee>) : EventDetailViewState

    object YesSelected : EventDetailViewState

    object NoSelected : EventDetailViewState

    object MaybeSelected : EventDetailViewState
}

