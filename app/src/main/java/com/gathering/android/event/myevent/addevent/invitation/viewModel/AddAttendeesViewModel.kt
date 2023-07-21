package com.gathering.android.event.myevent.addevent.invitation.viewModel

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import javax.inject.Inject

class AddAttendeesViewModel @Inject constructor() : ViewModel() {

    private val _viewState = ActiveMutableLiveData<AddAttendeesViewState>()
    val viewState: MutableLiveData<AddAttendeesViewState> by ::_viewState
    private var isAttendeeEmailValid: Boolean = false

    private var attendeesEmailList = mutableListOf<String>()

    fun onOKButtonClicked(attendeesEmailList: List<String>) {
        _viewState.setValue(AddAttendeesViewState.NavigateToAddEvent(attendeesEmailList))
    }

    fun onAttendeeEmailChanged(attendeeEmail: String) {
        isAttendeeEmailValid = isAttendeeEmailValid(attendeeEmail)
        val errorMessage =
            if (isAttendeeEmailValid) "" else INVALID_EMAIL_ADDRESS_FORMAT_ERROR_MESSAGE
        _viewState.setValue(AddAttendeesViewState.ShowError(errorMessage))
        checkIsEmailField()
    }

    fun onAddAttendeeButtonClicked(attendeeEmail: String) {
        if (attendeesEmailList.contains(attendeeEmail)) return
        attendeesEmailList.add(attendeeEmail)
        _viewState.setValue(AddAttendeesViewState.AddAttendeeToRecyclerView(attendeeEmail))
        _viewState.setValue(AddAttendeesViewState.CleaEditText)
        _viewState.setValue(AddAttendeesViewState.HideKeyboard)
    }

    fun onAttendeeRemoveItemClicked(attendeeEmail: String) {
        attendeesEmailList.remove(attendeeEmail)
        _viewState.setValue(AddAttendeesViewState.RemoveAttendeeFromRecyclerView(attendeeEmail))
    }

    fun onViewCreated(attendees: String?) {
        if (attendees.isNullOrEmpty()) return
        val attendeeList = attendees.split(",")
        attendeeList.forEach { item ->
            _viewState.setValue(AddAttendeesViewState.AddAttendeeToRecyclerView(item))
        }
    }


    private fun checkIsEmailField() {
        _viewState.setValue(AddAttendeesViewState.AddAttendeeButtonVisibility(isAttendeeEmailValid))
    }


    private fun isAttendeeEmailValid(email: String): Boolean {
        return !(TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    companion object {
        const val INVALID_EMAIL_ADDRESS_FORMAT_ERROR_MESSAGE = "Enter valid email address"
    }
}