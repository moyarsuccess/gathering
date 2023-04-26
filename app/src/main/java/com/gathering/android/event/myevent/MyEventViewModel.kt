package com.gathering.android.event.myevent

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import javax.inject.Inject

class MyEventViewModel @Inject constructor() : ViewModel() {

    private val _viewState = ActiveMutableLiveData<MyEventViewState>()
    val viewState: MutableLiveData<MyEventViewState> by ::_viewState

    fun onViewCreated() {
        _viewState.setValue(MyEventViewState.ShowProgress)
        //TODO we should show user's event as a list (call fun loadUserEvents)
    }

    fun onFabButtonClicked() {
        _viewState.setValue(MyEventViewState.NavigateToAddEvent)
    }
}