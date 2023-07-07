package com.gathering.android.event.myevent.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.ResponseState
import com.gathering.android.event.model.Event
import com.gathering.android.event.model.EventRepository
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class MyEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _viewState = ActiveMutableLiveData<MyEventViewState>()
    val viewState: MutableLiveData<MyEventViewState> by ::_viewState

    fun onResume() {
        _viewState.setValue(MyEventViewState.ShowProgress)
        eventRepository.getMyEvents { request ->
            when (request) {
                is ResponseState.Failure -> hideProgress()
                is ResponseState.Success<*> -> {
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

                is ResponseState.SuccessWithError<*> -> {
                    // TODO show proper error
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