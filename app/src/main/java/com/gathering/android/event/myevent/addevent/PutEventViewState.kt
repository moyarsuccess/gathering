package com.gathering.android.event.myevent.addevent

import com.gathering.android.event.Event

sealed interface PutEventViewState {

    class PutEventButtonVisibility(val isAddEventButtonEnabled: Boolean) : PutEventViewState
    class ShowError(val errorMessage: String?) : PutEventViewState
    class SetAddress(val address: String) : PutEventViewState
    class SetImage(val image: String) : PutEventViewState
    class SetAttendeeList(val attendees: String) : PutEventViewState
    class NavigateToInviteFriend(val attendeeList: List<String>) : PutEventViewState
    class NavigateToMyEvent(val event: Event) : PutEventViewState

    object NavigateToPutPic : PutEventViewState
    object NavigateToPutLocation : PutEventViewState
    object OpenTimePickerDialog : PutEventViewState
    object OpenDatePickerDialog : PutEventViewState

    object MorphPutEventButtonToProgress : PutEventViewState
    object RevertPutEventProgressToButton : PutEventViewState
}