package com.gathering.android.event.myevent.addevent

import com.gathering.android.event.Event

sealed interface AddEventViewState {

    class AddEventButtonVisibility(val isAddEventButtonEnabled: Boolean) : AddEventViewState
    class ShowError(val errorMessage: String?) : AddEventViewState
    class SetAddress(val address: String) : AddEventViewState
    class SetImage(val image: String) : AddEventViewState
    class SetAttendeeList(val attendees: String) : AddEventViewState
    class NavigateToInviteFriend(val attendeeList: List<String>) : AddEventViewState
    class NavigateToMyEvent(val event: Event) : AddEventViewState

    object NavigateToAddPic : AddEventViewState
    object NavigateToAddLocation : AddEventViewState
    object OpenTimePickerDialog : AddEventViewState
    object OpenDatePickerDialog : AddEventViewState

    object MorphAddEventButtonToProgress : AddEventViewState
    object RevertAddEventProgressToButton : AddEventViewState
}