package com.gathering.android.event.myevent

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.event.model.Event
import com.gathering.android.event.model.EventRepository
import com.gathering.android.event.model.EventRequest
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class MyEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _viewState = ActiveMutableLiveData<MyEventViewState>()
    val viewState: MutableLiveData<MyEventViewState> by ::_viewState

    fun onResume() {
        _viewState.setValue(MyEventViewState.ShowProgress)
        eventRepository.getAllEvents { request ->
            when (request) {
                is EventRequest.Failure -> hideProgress()
                is EventRequest.Success<*> -> {
                    (request.data as? List<Event>)?.also {
                        if (it.isEmpty()) {
                            _viewState.setValue(MyEventViewState.ShowNoData)
                            hideProgress()
                            return@also
                        }
                        _viewState.setValue(MyEventViewState.ShowUserEventList(it))
                        _viewState.setValue(MyEventViewState.HideNoData)
                        hideProgress()
                    } ?: run {
                        hideProgress()
                    }
                }
            }
        }
    }

    private fun hideProgress() {
        _viewState.setValue(MyEventViewState.HideProgress)
    }

    fun onFabButtonClicked() {
        _viewState.setValue(MyEventViewState.NavigateToAddEvent)
    }
}