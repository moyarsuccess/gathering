package com.gathering.android.event.attendeeDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.toImageUrl
import com.gathering.android.event.eventdetail.AcceptType
import com.gathering.android.event.model.AttendeeModel
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
        UiState(
            viewModelState.selectedAcceptType,
            viewModelState.attendeeModels,
            viewModelState.attendeeModels.isEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UiState()
    )
    data class ViewModelState(
        val selectedAcceptType: AcceptType = AcceptType.Yes,
        val attendeeModels: List<AttendeeModel> = emptyList()
    )

    data class UiState(
        val selectedAcceptType: AcceptType = AcceptType.Yes,
        val selectedAttendeesList: List<AttendeeModel> = emptyList(),
        val showNoData: Boolean = false
    )

    fun onViewCreated(attendeeModels: List<AttendeeModel>) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(attendeeModels = attendeeModels.filter {
                if (isComposeEnabled) {
                    it.accepted == AcceptType.Yes.type
                } else {
                    true
                }
            })
        }
    }

    fun onAcceptTypeSelectionChanged(acceptType: AcceptType) {
        viewModelState.update { currentViewState ->
            currentViewState.copy(selectedAcceptType = acceptType)
        }
    }
}