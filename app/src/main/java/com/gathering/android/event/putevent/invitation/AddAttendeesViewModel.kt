package com.gathering.android.event.putevent.invitation

import android.util.Log
import android.util.Patterns
import androidx.annotation.VisibleForTesting
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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var addAttendeeNavigator: AddAttendeeNavigator? = null

    private val viewModelState = MutableStateFlow(AddAttendeesViewModelState())
    val uiState: StateFlow<AddAttendeeUiState> = viewModelState.map { viewModelState ->
        AddAttendeeUiState(
            errorMessage = viewModelState.errorMessage,
            attendeeEmail = viewModelState.attendeeEmail,
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
                attendeeEmail = attendeeEmail,
                addAttendeeButtonEnable = isAttendeeEmailValid,
                errorMessage = ""
            )
        }
    }

    fun onAddAttendeeButtonClicked(attendeeEmail: String) {
        if (attendeeEmail.isEmpty() || !isAttendeeEmailValid(attendeeEmail)) {
            viewModelState.update { currentState ->
                currentState.copy(
                    errorMessage = EMAIL_IS_NOT_VALID,
                )
            }
            return
        }
        val emails = mutableSetOf<String>()
        emails.addAll(viewModelState.value.attendeesEmailList)
        emails.add(attendeeEmail)
        viewModelState.update { currentState ->
            currentState.copy(
                attendeesEmailList = emails,
                errorMessage = ""
            )
        }
    }

    fun onAttendeeRemoveItemClicked(attendeeEmail: String) {
        if (attendeeEmail.isEmpty()) {
            Log.e("WTF", "attendee email is empty")
            return
        }
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
        val attendeeEmail: String? = "",
        val attendeesEmailList: Set<String> = setOf(),
        val addAttendeeButtonEnable: Boolean = false,
        val errorMessage: String = "",
    )

    companion object {
        const val EMAIL_IS_NOT_VALID = "Email is empty or invalid. please try again."
    }
}