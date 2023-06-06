package com.gathering.android.event.myevent.addevent

import com.gathering.android.event.model.Event
import com.gathering.android.event.myevent.addevent.invitation.model.Contact

sealed interface AddEventViewState {

    class AddEventButtonVisibility(val isAddEventButtonEnabled: Boolean) : AddEventViewState
    class ShowError(val errorMessage: String?) : AddEventViewState
    class SetAddress(val address: String) : AddEventViewState
    class SetImage(val image: String) : AddEventViewState
    class SetAttendeeList(val attendees: String) : AddEventViewState
    class NavigateToInviteFriend(val contactList: List<Contact>) : AddEventViewState
    class NavigateToMyEvent(val event: Event) : AddEventViewState

    object NavigateToAddPic : AddEventViewState
    object NavigateToAddLocation : AddEventViewState
    object OpenTimePickerDialog : AddEventViewState
    object OpenDatePickerDialog : AddEventViewState

    object MorphAddEventButtonToProgress : AddEventViewState
    object RevertAddEventProgressToButton : AddEventViewState
}