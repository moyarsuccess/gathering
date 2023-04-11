package com.gathering.android.event.eventdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.event.home.model.Event
import javax.inject.Inject

class EventDetailViewModel @Inject constructor() : ViewModel() {

    private val _viewState = ActiveMutableLiveData<EventDetailViewState>()
    val viewState: LiveData<EventDetailViewState> by ::_viewState

    fun onViewCreated(event: Event) {
        _viewState.setValue(EventDetailViewState.ShowEventDetail(event))
    }

    fun onAcceptButtonClicked() {
        //TODO We don't have a road map for Accept button clicked action yet
    }

    fun onDeclineButtonClicked() {
        //TODO We don't have a road map for Decline button clicked action yet
    }
}
