package com.gathering.android.event.rsvpDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.event.GENERAL_ERROR
import com.gathering.android.event.SERVER_NOT_RESPONDING_TO_SHOW_EVENTS
import com.gathering.android.event.model.AttendeeModel
import com.gathering.android.event.repo.EventRepository
import com.gathering.android.event.toEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class RsvpListViewModel @Inject constructor(private val eventRepository: EventRepository) :
    ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is RsvpListException -> {
                when (throwable) {
                    RsvpListException.ServerNotRespondingException -> SERVER_NOT_RESPONDING_TO_SHOW_EVENTS
                    is RsvpListException.GeneralException -> GENERAL_ERROR
                }
            }

            else -> {
                GENERAL_ERROR
            }
        }
        _uiState.update { currentState ->
            currentState.copy(
                errorMessage = errorMessage,
                showNoData = currentState.attendeeModels.isEmpty()
            )
        }
    }

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
        val attendeeModels: List<AttendeeModel> = emptyList()
    )

    fun onViewCreated(eventId: Long) {
        _uiState.update { currentState ->
            currentState.copy(showNoData = true)
        }
        viewModelScope.launch(exceptionHandler) {
            val event = eventRepository.getEventById(eventId).toEvent()
            val sortedAttendees = sortAttendeesByAccepted(event.attendeeModels)
            _uiState.update { currentViewState ->
                currentViewState.copy(
                    imageUri = event.photoUrl,
                    eventName = event.eventName,
                    attendeeModels = sortedAttendees,
                    showNoData = false,
                )
            }

        }
    }

    private fun sortAttendeesByAccepted(attendeeModels: List<AttendeeModel>): List<AttendeeModel> {
        return attendeeModels.sortedBy { it.accepted }
    }
}
