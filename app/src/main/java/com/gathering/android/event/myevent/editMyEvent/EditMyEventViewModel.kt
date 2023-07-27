package com.gathering.android.event.myevent.editMyEvent

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.event.model.repo.EventRepository
import javax.inject.Inject

class EditMyEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _viewState = ActiveMutableLiveData<EditMyEventViewState>()
    val viewState: MutableLiveData<EditMyEventViewState> by ::_viewState

}