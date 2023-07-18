package com.gathering.android.event.myevent

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.ResponseState
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.model.repo.EventRepository
import com.gathering.android.event.toEvent
import javax.inject.Inject

class MyEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _viewState = ActiveMutableLiveData<MyEventViewState>()
    val viewState: MutableLiveData<MyEventViewState> by ::_viewState

    fun onViewCreated() {
        _viewState.setValue(MyEventViewState.ShowProgress)
        eventRepository.getMyEvents { request ->
            when (request) {
                is ResponseState.Failure -> {
                    Log.d("WTF_SWE", request.throwable.message ?: "")
                    _viewState.setValue(MyEventViewState.ShowNoData)
                    hideProgress()
                }

                is ResponseState.Success<List<EventModel>> -> {
                    val eventModelList = request.data as? List<EventModel>
                    (eventModelList)
                        ?.map { it.toEvent() }
                        ?.also { events ->
                            if (events.isEmpty()) {
                                _viewState.setValue(MyEventViewState.ShowNoData)
                                hideProgress()
                                return@also
                            }
                            _viewState.setValue(MyEventViewState.ShowUserEventList(events))
                            _viewState.setValue(MyEventViewState.HideNoData)
                            hideProgress()
                        } ?: run {
                        _viewState.setValue(MyEventViewState.ShowNoData)
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