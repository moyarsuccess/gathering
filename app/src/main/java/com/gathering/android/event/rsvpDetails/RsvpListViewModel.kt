package com.gathering.android.event.rsvpDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.event.model.Attendee
import com.gathering.android.event.repo.EventRepository
import com.gathering.android.event.toEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class RsvpListViewModel @Inject constructor(private val eventRepository: EventRepository) :
    ViewModel() {

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
        val errorMessage: String? = null,
        val attendees: List<Attendee> = emptyList()
    )

    private fun updateShowNoData() {
        _uiState.update { currentViewState ->
            currentViewState.copy(
                showNoData = currentViewState.attendees.isEmpty()
            )
        }
    }

    fun onViewCreated(eventId: Long) {
        viewModelScope.launch {
            val event = try {
                eventRepository.getEventById(eventId).toEvent()
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(errorMessage = SERVER_ERROR)
                }
                Log.d("XXX:", e.toString())
                return@launch
            }
            _uiState.update { currentViewState ->
                currentViewState.copy(
                    imageUri = event.photoUrl,
                    eventName = event.eventName,
                    attendees = event.attendees
                )
            }
            updateShowNoData()
        }
    }

    companion object {
        private const val SERVER_ERROR = "Can not catch the server at this time"
    }
}
