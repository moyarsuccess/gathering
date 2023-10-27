package com.gathering.android.event.putevent.invitation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class AddAttendeesViewModel @Inject constructor() : ViewModel() {

    private var addAttendeeNavigator: AddAttendeeNavigator? = null

    private val viewModelState = MutableStateFlow(AddAttendeesViewModelState())
    val uiState: StateFlow<AddAttendeeUiState> = viewModelState.map { viewModelState ->
        AddAttendeeUiState(
            attendeesEmailList = viewModelState.attendeesEmailList.toList(),
            addAttendeeButtonEnable = viewModelState.addAttendeeButtonEnable,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AddAttendeeUiState()
    )

    fun onViewCreated(attendees: String?, addAttendeeNavigator: AddAttendeeNavigator) {
        this.addAttendeeNavigator = addAttendeeNavigator
        if (attendees.isNullOrEmpty()) return

        viewModelState.update { currentState ->
            currentState.copy(
                attendeesEmailList = attendees.split(",").toSet(),
            )
        }
    }

    fun onOKButtonClicked() {
        addAttendeeNavigator?.navigateToAddEvent(viewModelState.value.attendeesEmailList.toList())
    }

    fun onAttendeeEmailChanged(attendeeEmail: String) {
        val isAttendeeEmailValid = isAttendeeEmailValid(attendeeEmail)
        viewModelState.update { currentState ->
            currentState.copy(
                addAttendeeButtonEnable = isAttendeeEmailValid
            )
        }
    }

    fun onAddAttendeeButtonClicked(attendeeEmail: String) {
        val emails = mutableSetOf<String>()
        emails.addAll(viewModelState.value.attendeesEmailList)
        emails.add(attendeeEmail)

        viewModelState.update { currentState ->
            currentState.copy(
                attendeesEmailList = emails,
            )
        }
    }

    fun onAttendeeRemoveItemClicked(attendeeEmail: String) {
        val emails = mutableSetOf<String>()
        emails.addAll(viewModelState.value.attendeesEmailList)
        emails.remove(attendeeEmail)

        viewModelState.update { currentState ->
            currentState.copy(
                attendeesEmailList = emails
            )
        }
    }

    private fun isAttendeeEmailValid(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    data class AddAttendeesViewModelState(
        val attendeesEmailList: Set<String> = setOf(),
        val addAttendeeButtonEnable: Boolean = false,
    )
}