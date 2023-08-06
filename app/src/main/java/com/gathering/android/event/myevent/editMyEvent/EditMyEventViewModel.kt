package com.gathering.android.event.myevent.editMyEvent

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.ResponseState
import com.gathering.android.event.Event
import com.gathering.android.event.model.repo.EventRepository
import com.gathering.android.event.myevent.MyEventViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class EditMyEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _viewState = ActiveMutableLiveData<EditMyEventViewState>()
    val viewState: MutableLiveData<EditMyEventViewState> by ::_viewState

    @RequiresApi(Build.VERSION_CODES.O)
    fun onViewCreated(event: Event) {
        showEventDetails(event)
    }

    fun onEditButtonClicked(event: Event) {
        eventRepository.editEvent(event) {
            when (it) {
                is ResponseState.Failure -> {
                    _viewState.setValue(EditMyEventViewState.ShowError(MyEventViewModel.UPDATE_EVENT_REQUEST_FAILED))
                }

                is ResponseState.Success -> {
                    _viewState.setValue(EditMyEventViewState.NavigateToMyEvent(event))
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showEventDetails(event: Event) {
        val dateTime = timestampToDateTime(event.dateAndTime)
        val date = dateTime.split(" ")[0]
        val time = dateTime.split(" ")[1]

        _viewState.setValue(EditMyEventViewState.SetPhoto(event.photoUrl))
        _viewState.setValue(EditMyEventViewState.SetEventName(event.eventName))
        _viewState.setValue(EditMyEventViewState.SetDate(date))
        _viewState.setValue(EditMyEventViewState.SetTime(time))
        _viewState.setValue(EditMyEventViewState.SetDescription(event.description))
        _viewState.setValue(EditMyEventViewState.SetLocation(event.location))
        _viewState.setValue(EditMyEventViewState.SetAttendees(event.attendees))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun timestampToDateTime(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return dateTime.format(formatter)
    }
}