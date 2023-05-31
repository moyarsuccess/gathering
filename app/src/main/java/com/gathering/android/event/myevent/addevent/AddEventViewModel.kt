package com.gathering.android.event.myevent.addevent

import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.event.home.model.Event
import com.gathering.android.event.myevent.addevent.invitation.model.Contact
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    fun onTimeButtonClicked() {
        _viewState.setValue(AddEventViewState.OpenTimePickerDialog)
    }

    fun onDateButtonClicked() {
        _viewState.setValue(AddEventViewState.OpenDatePickerDialog)
    }

    fun onAddEventButtonClicked(event: Event) {
        addEventToFireStore(event)
    }

    fun onAddressChanged(address: String) {
        _viewState.setValue(AddEventViewState.SetAddress(address))
        checkAllFieldsReady()
    }

    fun onDescriptionChanged(description: String) {
        checkAllFieldsReady()
    }

    fun onAttendeeListChanged(contacts: List<Contact>) {
        contactList.clear()
        contactList.addAll(contacts)
        val attendee = contacts.joinToString(",")
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

    private fun addEventToFireStore(event: Event) {
        val db = Firebase.firestore

        db.collection("Events")
            .add(event)
            .addOnSuccessListener {
                _viewState.setValue(AddEventViewState.NavigateToMyEvent(event))
            }
            .addOnFailureListener {
                _viewState.setValue(AddEventViewState.NavigateToMyEvent(event))
            }
    }

    fun onImageSelected(image: String?) {
        if (image == null) return
        _viewState.setValue(AddEventViewState.SetImage(image))
    }

}