package com.gathering.android.event.myevent.addevent

import com.gathering.android.event.home.model.Event

sealed interface AddEventViewState {

    class AddEventButtonVisibility(val isAddEventButtonEnabled: Boolean) : AddEventViewState
    class NavigateToMyEvent(event: Event) : AddEventViewState

    class SetAddress(val address: String) : AddEventViewState
    class ShowError(val errorMessage: String) : AddEventViewState

    object NavigateToAddPic : AddEventViewState
    object NavigateToAddLocation : AddEventViewState
    object NavigateToInviteFriend : AddEventViewState
}