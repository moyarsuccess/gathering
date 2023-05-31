package com.gathering.android.event.myevent

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.event.home.model.Event
import javax.inject.Inject

class MyEventViewModel @Inject constructor() : ViewModel() {

    private val _viewState = ActiveMutableLiveData<MyEventViewState>()
    val viewState: MutableLiveData<MyEventViewState> by ::_viewState

    fun onViewCreated(eventList: List<Event>) {
        _viewState.setValue(MyEventViewState.ShowProgress)
        if (eventList.isNotEmpty()) {
            _viewState.setValue(MyEventViewState.ShowUserEventList(eventList))
            _viewState.setValue(MyEventViewState.HideNoData)
        } else {
            _viewState.setValue(MyEventViewState.ShowNoData)
        }
        _viewState.setValue(MyEventViewState.HideProgress)
    }

    fun onFabButtonClicked() {
        _viewState.setValue(MyEventViewState.NavigateToAddEvent)
    }
}