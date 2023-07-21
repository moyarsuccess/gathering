package com.gathering.android.event.myevent.addevent.invitation.viewModel

sealed interface AddAttendeesViewState {

    class NavigateToAddEvent(val attendeeList: List<String>) : AddAttendeesViewState

    class ShowError(val errorMessage: String) : AddAttendeesViewState

    class SetAttendee(val attendee: String) : AddAttendeesViewState

    class AddAttendeeToRecyclerView(val attendee: String) : AddAttendeesViewState

    class RemoveAttendeeFromRecyclerView(val attendee: String) : AddAttendeesViewState

    class AddAttendeeButtonVisibility(val isAddAttendeeButtonEnabled: Boolean) : AddAttendeesViewState

    object CleaEditText : AddAttendeesViewState

    object HideKeyboard : AddAttendeesViewState

}