package com.gathering.android.event.eventdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.ResponseState
import com.gathering.android.common.UserRepo
import com.gathering.android.event.AcceptTypeRepository
import com.gathering.android.event.Event
import com.gathering.android.event.myevent.AcceptType
import javax.inject.Inject

class EventDetailViewModel @Inject constructor(
    private val acceptTypeRepository: AcceptTypeRepository,
    private val userRepo: UserRepo
) : ViewModel() {


    private val _viewState = ActiveMutableLiveData<EventDetailViewState>()
    val viewState: LiveData<EventDetailViewState> by ::_viewState

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

    fun onYesButtonClicked(currentUserId: String) {

        event?.let {
            acceptTypeRepository.setEventAcceptType(
                it.eventId,
                AcceptType.Yes,
            ) { state ->
                when (state) {
                    is ResponseState.Success -> {
                        // todo select the yes button
                    }
                    is ResponseState.Failure -> {
                        //TODO() error state
                    }
                }
            }
        } ?: {
            throw Exception("event was not provided by calling onViewCreated")
        }

    }

    fun onNoButtonClicked(currentUserId: String) {
        TODO("Not yet implemented")

    }

    fun onMaybeButtonClicked(currentUserId: String) {
        TODO("Not yet implemented")
    }
}
