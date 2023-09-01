package com.gathering.android.event.eventdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.ResponseState
import com.gathering.android.common.UserRepo
import com.gathering.android.event.Event
import com.gathering.android.event.eventdetail.acceptrepo.AcceptTypeRepository
import javax.inject.Inject

class EventDetailViewModel @Inject constructor(
    private val acceptTypeRepository: AcceptTypeRepository,
    private val userRepo: UserRepo
) : ViewModel() {

    private val _viewState = ActiveMutableLiveData<EventDetailViewState>()
    val viewState: MutableLiveData<EventDetailViewState> by ::_viewState

    private var event: Event? = null
    fun onViewCreated(event: Event) {
        this.event = event
        _viewState.setValue(EventDetailViewState.ShowEventDetail(event))
        val currentUser = event.attendees.find { attendee ->
            attendee.email == userRepo.getUser()?.email
        }

        when (currentUser?.accepted) {
            AcceptType.Yes.type -> {
                _viewState.setValue(EventDetailViewState.YesSelected)
            }
            AcceptType.No.type -> {
                _viewState.setValue(EventDetailViewState.NoSelected)
            }
            AcceptType.Maybe.type -> {
                _viewState.setValue(EventDetailViewState.MaybeSelected)
            }
        }
    }

    fun onYesButtonClicked() {

        event?.let {
            acceptTypeRepository.setEventAcceptType(
                it.eventId,
                AcceptType.Yes,
            ) { result ->
                when (result) {
                    is ResponseState.Success -> {
                        setAcceptTypeOnSpecificEvent(it, AcceptType.Yes)
                        _viewState.setValue(EventDetailViewState.YesSelected)
                    }
                    is ResponseState.Failure -> {
                        _viewState.setValue(EventDetailViewState.ShowError("failed to select yes"))
                    }
                }
            }
        } ?: {
            throw Exception("event was not provided by calling onViewCreated")
        }

    }

    fun onNoButtonClicked() {
        event?.let {
            acceptTypeRepository.setEventAcceptType(
                it.eventId,
                AcceptType.No,
            ) { result ->
                when (result) {
                    is ResponseState.Success -> {
                        setAcceptTypeOnSpecificEvent(it, AcceptType.No)
                        _viewState.setValue(EventDetailViewState.NoSelected)
                    }
                    is ResponseState.Failure -> {
                        _viewState.setValue(EventDetailViewState.ShowError("failed to select no"))
                    }
                }
            }
        } ?: run {
            throw Exception("event was not provided by calling onViewCreated")
        }
    }

    fun onMaybeButtonClicked() {
        event?.let {
            acceptTypeRepository.setEventAcceptType(
                it.eventId,
                AcceptType.Maybe,
            ) { result ->
                when (result) {
                    is ResponseState.Success -> {
                        setAcceptTypeOnSpecificEvent(it, AcceptType.Maybe)
                        _viewState.setValue(EventDetailViewState.MaybeSelected)
                    }
                    is ResponseState.Failure -> {
                        _viewState.setValue(EventDetailViewState.ShowError("failed to select maybe"))
                    }
                }
            }
        } ?: run {
            throw Exception("event was not provided by calling onViewCreated")
        }
    }

    private fun setAcceptTypeOnSpecificEvent(it: Event, acceptType: AcceptType) {
        val currentUser = userRepo.getUser()
        val currentUserFoundAttendee =
            it.attendees.find { attendee -> attendee.email == currentUser?.email }
        currentUserFoundAttendee?.accepted = acceptType.type
    }

    fun onTvAttendeesDetailsClicked() {
        _viewState.setValue(
            EventDetailViewState.NavigateToAttendeesDetailBottomSheet(
                event?.attendees ?: emptyList()
            )
        )
    }
}
