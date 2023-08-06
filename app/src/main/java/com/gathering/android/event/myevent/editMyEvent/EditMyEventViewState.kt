package com.gathering.android.event.myevent.editMyEvent

import com.gathering.android.event.Event
import com.gathering.android.event.model.EventLocation

sealed interface EditMyEventViewState {
    class ShowError(val errorMessage: String) : EditMyEventViewState
    class NavigateToMyEvent(val event: Event) : EditMyEventViewState
    class SetPhoto(val photo: String) : EditMyEventViewState
    class SetEventName(val eventName: String) : EditMyEventViewState
    class SetTime(val time: String) : EditMyEventViewState
    class SetDate(val date: String) : EditMyEventViewState
    class SetDescription(val description: String) : EditMyEventViewState
    class SetLocation(val Location: EventLocation) : EditMyEventViewState
    class SetAttendees(val attendees: List<String>) : EditMyEventViewState
}