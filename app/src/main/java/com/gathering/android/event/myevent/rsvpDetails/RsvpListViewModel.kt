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

class RsvpListViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
        .stateIn(
            scope = viewModelScope,
            initialValue = UiState(),
            started = SharingStarted.Eagerly
        )

    data class UiState(
        val eventName: String? = null,
        val imageUri: String? = null,
        val showNoData: Boolean = false,
        val attendees: List<Attendee> = emptyList()
    )

    private fun updateShowNoData() {
        _uiState.update { currentViewState ->
            currentViewState.copy(
                showNoData = currentViewState.attendees.isEmpty()
            )
        }
    }

    fun onViewCreated(event: Event?) {
        if (event != null) {
            _uiState.update { currentViewState ->
                currentViewState.copy(
                    imageUri = event.photoUrl,
                    eventName = event.eventName
                )
            }
            updateShowNoData()
        }
    }
}
