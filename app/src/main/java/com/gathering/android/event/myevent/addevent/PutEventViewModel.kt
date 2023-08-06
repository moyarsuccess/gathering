package com.gathering.android.event.myevent.addevent

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.ResponseState
import com.gathering.android.event.Event
import com.gathering.android.event.myevent.addevent.repo.AddEventRepository
import javax.inject.Inject

class PutEventViewModel @Inject constructor(
    private val eventRepository: AddEventRepository
) : ViewModel() {

    private val _viewState = ActiveMutableLiveData<PutEventViewState>()
    val viewState: LiveData<PutEventViewState> by ::_viewState

    private var isImageFilled: Boolean = false
    private var isEventNameFilled: Boolean = false
    private var isDescriptionFilled: Boolean = false
    private var isDateFilled: Boolean = false
    private var isTimeFilled: Boolean = false
    private var isAddressFilled: Boolean = false
    private var isAttendeeListFilled: Boolean = false

    private val attendeesEmailList = mutableListOf<String>()

    fun onViewCreated() {
        _viewState.setValue(PutEventViewState.PutEventButtonVisibility(false))
    }

    fun onImageSelected(imageUrl: String?) {
        if (imageUrl == null) return
        _viewState.setValue(PutEventViewState.SetImage(imageUrl))
        isImageFilled = isImageFiled(imageUrl)
        val errorMessage = if (isEventNameFilled) null else IMAGE_NOT_FILLED_MESSAGE
        _viewState.setValue(PutEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onImageButtonClicked() {
        _viewState.setValue(PutEventViewState.NavigateToPutPic)
    }

    fun onDateButtonClicked() {
        _viewState.setValue(PutEventViewState.OpenDatePickerDialog)
    }

    fun onTimeButtonClicked() {
        _viewState.setValue(PutEventViewState.OpenTimePickerDialog)
    }

    fun onLocationButtonClicked() {
        _viewState.setValue(PutEventViewState.NavigateToPutLocation)
    }

    fun onInvitationButtonClicked() {
        _viewState.setValue(PutEventViewState.NavigateToInviteFriend(attendeesEmailList))
    }

    fun onAddEventButtonClicked(event: Event) {
        _viewState.setValue(PutEventViewState.MorphPutEventButtonToProgress)
        eventRepository.addEvent(event) { eventRequest ->
            when (eventRequest) {
                is ResponseState.Failure -> {
                    _viewState.setValue(PutEventViewState.ShowError(EVENT_REQUEST_FAILED))
                }

                is ResponseState.Success<String> -> {
                    _viewState.setValue(PutEventViewState.RevertPutEventProgressToButton)
                    _viewState.setValue(PutEventViewState.NavigateToMyEvent(event))
                }
            }
        }
    }

    fun onEventNameChanged(eventName: String) {
        isEventNameFilled = isEventNameFiled(eventName)
        val errorMessage = if (isEventNameFilled) null else EVENT_NAME_NOT_FILLED_MESSAGE
        _viewState.setValue(PutEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onDescriptionChanged(description: String) {
        isDescriptionFilled = isDescriptionFiled(description)
        val errorMessage = if (isEventNameFilled) null else DESCRIPTION_NOT_FILLED_MESSAGE
        _viewState.setValue(PutEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onDateChanged(date: String) {
        isDateFilled = isDateFiled(date)
        val errorMessage = if (isEventNameFilled) null else DATE_NOT_FILLED_MESSAGE
        _viewState.setValue(PutEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onTimeChanged(time: String) {
        isTimeFilled = isTimeFiled(time)
        val errorMessage = if (isEventNameFilled) null else TIME_NOT_FILLED_MESSAGE
        _viewState.setValue(PutEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onAddressChanged(address: String) {
        _viewState.setValue(PutEventViewState.SetAddress(address))
        isAddressFilled = isAddressFiled(address)
        val errorMessage = if (isEventNameFilled) null else ADDRESS_NOT_FILLED_MESSAGE
        _viewState.setValue(PutEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onAttendeeListChanged(contacts: List<String>) {
        attendeesEmailList.clear()
        attendeesEmailList.addAll(contacts)
        val attendee = contacts.joinToString(",")
        _viewState.setValue(PutEventViewState.SetAttendeeList(attendee))

        isAttendeeListFilled = isAttendeesFiled(attendee)
        val errorMessage = if (isEventNameFilled) null else ATTENDEES_NOT_FILLED_MESSAGE
        _viewState.setValue(PutEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    private fun isImageFiled(imageUrl: String): Boolean {
        return imageUrl.isNotEmpty() && imageUrl.isNotBlank()
    }

    private fun isEventNameFiled(eventName: String): Boolean {
        return eventName.isNotEmpty() && eventName.isNotBlank()
    }

    private fun isDescriptionFiled(description: String): Boolean {
        return description.isNotEmpty() && description.isNotBlank()
    }

    private fun isDateFiled(date: String): Boolean {
        return date.isNotEmpty() && date.isNotBlank()
    }

    private fun isTimeFiled(time: String): Boolean {
        return time.isNotEmpty() && time.isNotBlank()
    }

    private fun isAddressFiled(address: String): Boolean {
        return address.isNotEmpty() && address.isNotBlank()
    }

    private fun isAttendeesFiled(attendeeList: String): Boolean {
        return attendeeList.isNotEmpty() && attendeeList.isNotBlank()
    }

    private fun checkAllFieldsReady() {
        _viewState.setValue(PutEventViewState.PutEventButtonVisibility(isAllFieldsFilled()))
    }

    private fun isAllFieldsFilled(): Boolean {
        return isImageFilled &&
                isEventNameFilled &&
                isDescriptionFilled &&
                isDateFilled &&
                isTimeFilled &&
                isAddressFilled
    }

    companion object {
        private const val IMAGE_NOT_FILLED_MESSAGE = "Please pick or take a picture"
        private const val EVENT_NAME_NOT_FILLED_MESSAGE = "Please enter event name"
        private const val DESCRIPTION_NOT_FILLED_MESSAGE = "Please enter description"
        private const val DATE_NOT_FILLED_MESSAGE = "Please select a valid date "
        private const val TIME_NOT_FILLED_MESSAGE = "Please select a valid time"
        private const val ADDRESS_NOT_FILLED_MESSAGE = "Please select a address"
        private const val ATTENDEES_NOT_FILLED_MESSAGE = "Please select attendees"
        private const val EVENT_REQUEST_FAILED = "EVENT_REQUEST_FAILED"
    }
}