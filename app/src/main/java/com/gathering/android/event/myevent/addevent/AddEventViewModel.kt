package com.gathering.android.event.myevent.addevent

import android.util.Log
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.event.home.model.Event
import com.gathering.android.event.myevent.addevent.invitation.model.Contact
import javax.inject.Inject

class AddEventViewModel @Inject constructor() : ViewModel() {

    private val _viewState = ActiveMutableLiveData<AddEventViewState>()
    val viewState: ActiveMutableLiveData<AddEventViewState> by ::_viewState

    private val contactList = mutableListOf<Contact>()

    fun onImageButtonClicked() {
        _viewState.setValue(AddEventViewState.NavigateToAddPic)
    }

    fun onLocationButtonClicked() {
        _viewState.setValue(AddEventViewState.NavigateToAddLocation)

    }

    fun onInvitationButtonClicked() {
        _viewState.setValue(AddEventViewState.NavigateToInviteFriend(contactList))
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

    fun onAttendeeListChanged(contacts: List<Contact>) {
        Log.i("WTF2", contacts.toString())
        contactList.clear()
        contactList.addAll(contacts)
        val attendee = contacts.joinToString(",")
        Log.i("WTF2", attendee)
        _viewState.setValue(AddEventViewState.SetAttendeeList(attendee))
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