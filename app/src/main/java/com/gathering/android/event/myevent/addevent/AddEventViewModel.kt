package com.gathering.android.event.myevent.addevent

import android.provider.CalendarContract.Attendees
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.event.home.model.Event
import javax.inject.Inject

class AddEventViewModel @Inject constructor() : ViewModel() {

    private val _viewState = ActiveMutableLiveData<AddEventViewState>()
    val viewState: ActiveMutableLiveData<AddEventViewState> by ::_viewState

    fun onImageButtonClicked() {
        _viewState.setValue(AddEventViewState.NavigateToAddPic)
    }

    fun onLocationButtonClicked() {
        _viewState.setValue(AddEventViewState.NavigateToAddLocation)

    }

    fun onInvitationButtonClicked() {
        _viewState.setValue(AddEventViewState.NavigateToInviteFriend)
    }

    fun onAddEventButtonClicked(event: Event) {
        _viewState.setValue(AddEventViewState.NavigateToMyEvent(event))
    }

    fun onAddressChanged(address: String) {
        _viewState.setValue(AddEventViewState.SetAddress(address))
        checkAllFieldsReady()
    }

    fun onDescriptionChanged(description: String) {
        checkAllFieldsReady()
    }

    fun onMinAttendeeChanged(minAttendee: String) {
        checkAllFieldsReady()
    }

    fun onMaxAttendeeChanged(maxAttendees: Attendees) {
        checkAllFieldsReady()
    }

    private fun checkAllFieldsReady() {
        _viewState.setValue(AddEventViewState.AddEventButtonVisibility(isAllFieldsValid()))
    }

    private fun isAllFieldsValid(): Boolean {
        //TODO We should check if all the field have been field or not
        return true
    }
}