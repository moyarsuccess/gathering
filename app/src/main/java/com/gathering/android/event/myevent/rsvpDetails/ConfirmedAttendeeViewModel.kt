package com.gathering.android.event.myevent.rsvpDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.event.Event
import com.gathering.android.event.model.Attendee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ConfirmedAttendeeViewModel @Inject constructor() : ViewModel() {

    private val viewModelState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = viewModelState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UiState()
        )

    data class UiState(
        val eventName: String? = null,
        val imageUri: String? = null,
        val showNoData: Boolean = false,
        val attendees: List<Attendee> = emptyList(),
    )

    fun onViewCreated(event: Event?) {
        if (event != null) {
            viewModelState.update { currentViewState ->
                currentViewState.copy(
                    imageUri = event.photoUrl,
                    eventName = event.eventName
                )
            }
            updateShowNoData()
        }
    }

    private fun updateShowNoData() {
        viewModelState.update { currentViewState ->
            currentViewState.copy(
                showNoData = currentViewState.attendees.isEmpty()
            )
        }
    }
    fun getGoingAttendees(): List<Attendee> {
        return viewModelState.value.attendees.filter { it.accepted == "GOING" }
    }

    fun getNotGoingAttendees(): List<Attendee> {
        return viewModelState.value.attendees.filter { it.accepted == "NOT GOING" }
    }

    fun getMaybeAttendees(): List<Attendee> {
        return viewModelState.value.attendees.filter { it.accepted == "MAYBE" }
    }
}