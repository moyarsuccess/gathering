package com.gathering.android.event.attendeeDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.event.eventdetail.AcceptType
import com.gathering.android.event.model.Attendee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class AttendeesDetailViewModel @Inject constructor() : ViewModel() {

    private val viewModelState = MutableStateFlow(ViewModelState())

    val uiState: Flow<UiState> = viewModelState.map { viewModelState ->
        val selectedAttendeesList = viewModelState.attendees.filter { attendee ->
            attendee.accepted == viewModelState.selectedAcceptType.type
        }
        val selectedAttendeeEmails = selectedAttendeesList.map { attendee ->
            attendee.email ?: ""
        }
        UiState(
            selectedAttendeeEmails,
            viewModelState.selectedAcceptType,
            selectedAttendeesList,
            selectedAttendeesList.isEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UiState()
    )

    data class ViewModelState(
        val selectedAcceptType: AcceptType = AcceptType.Yes,
        val attendees: List<Attendee> = emptyList()
    )

    data class UiState(
        val selectedAttendeeEmails: List<String> = emptyList(),
        val selectedAcceptType: AcceptType = AcceptType.Yes,
        val selectedAttendeesList: List<Attendee> = emptyList(),
        val showNoData: Boolean = false
    )

    fun onViewCreated(attendees: List<Attendee>) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(attendees = attendees)
        }
    }

    fun onAcceptTypeSelectionChanged(acceptType: AcceptType) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(selectedAcceptType = acceptType)
        }
    }
}