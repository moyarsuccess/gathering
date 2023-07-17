package com.gathering.android.event.myevent.addevent

import androidx.lifecycle.ViewModel
import com.gathering.android.common.ActiveMutableLiveData
import com.gathering.android.common.ResponseState
import com.gathering.android.event.model.Event
import com.gathering.android.event.myevent.addevent.invitation.model.Contact
import com.gathering.android.event.myevent.addevent.repo.AddEventRepository
import javax.inject.Inject

class AddEventViewModel @Inject constructor(
    private val eventRepository: AddEventRepository
) : ViewModel() {

    private val _viewState = ActiveMutableLiveData<AddEventViewState>()
    val viewState: ActiveMutableLiveData<AddEventViewState> by ::_viewState

    private var isImageFilled: Boolean = false
    private var isEventNameFilled: Boolean = false
    private var isDescriptionFilled: Boolean = false
    private var isDateFilled: Boolean = false
    private var isTimeFilled: Boolean = false
    private var isAddressFilled: Boolean = false
    private var isAttendeeListFilled: Boolean = false

    private val contactList = mutableListOf<Contact>()

    fun onViewCreated() {
        _viewState.setValue(AddEventViewState.AddEventButtonVisibility(false))
    }

    fun onImageSelected(imageUrl: String?) {
        if (imageUrl == null) return
        _viewState.setValue(AddEventViewState.SetImage(imageUrl))
        isImageFilled = isImageFiled(imageUrl)
        val errorMessage = if (isEventNameFilled) null else IMAGE_NOT_FILLED_MESSAGE
        _viewState.setValue(AddEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onImageButtonClicked() {
        _viewState.setValue(AddEventViewState.NavigateToAddPic)
    }

    fun onDateButtonClicked() {
        _viewState.setValue(AddEventViewState.OpenDatePickerDialog)
    }

    fun onTimeButtonClicked() {
        _viewState.setValue(AddEventViewState.OpenTimePickerDialog)
    }

    fun onLocationButtonClicked() {
        _viewState.setValue(AddEventViewState.NavigateToAddLocation)
    }

    fun onInvitationButtonClicked() {
        _viewState.setValue(AddEventViewState.NavigateToInviteFriend(contactList))
    }

    fun onAddEventButtonClicked(event: Event) {
        _viewState.setValue(AddEventViewState.MorphAddEventButtonToProgress)
        eventRepository.addEvent(event) { eventRequest ->
            when (eventRequest) {
                is ResponseState.Failure -> viewState.setValue(AddEventViewState.ShowError("Event Request failed "))
                is ResponseState.Success<*> -> {
                    _viewState.setValue(AddEventViewState.RevertAddEventProgressToButton)
                    _viewState.setValue(AddEventViewState.NavigateToMyEvent(event))
                }

                is ResponseState.SuccessWithError<*> -> {
                    // TODO show proper error
                }
            }
        }
    }

    fun onEventNameChanged(eventName: String) {
        isEventNameFilled = isEventNameFiled(eventName)
        val errorMessage = if (isEventNameFilled) null else EVENT_NAME_NOT_FILLED_MESSAGE
        _viewState.setValue(AddEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onDescriptionChanged(description: String) {
        isDescriptionFilled = isDescriptionFiled(description)
        val errorMessage = if (isEventNameFilled) null else DESCRIPTION_NOT_FILLED_MESSAGE
        _viewState.setValue(AddEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onDateChanged(date: String) {
        isDateFilled = isDateFiled(date)
        val errorMessage = if (isEventNameFilled) null else DATE_NOT_FILLED_MESSAGE
        _viewState.setValue(AddEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onTimeChanged(time: String) {
        isTimeFilled = isTimeFiled(time)
        val errorMessage = if (isEventNameFilled) null else TIME_NOT_FILLED_MESSAGE
        _viewState.setValue(AddEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onAddressChanged(address: String) {
        _viewState.setValue(AddEventViewState.SetAddress(address))
        isAddressFilled = isAddressFiled(address)
        val errorMessage = if (isEventNameFilled) null else ADDRESS_NOT_FILLED_MESSAGE
        _viewState.setValue(AddEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onAttendeeListChanged(contacts: List<Contact>) {
        contactList.clear()
        contactList.addAll(contacts)
        val attendee = contacts.joinToString(",")
        _viewState.setValue(AddEventViewState.SetAttendeeList(attendee))

        isAttendeeListFilled = isAttendeesFiled(attendee)
        val errorMessage = if (isEventNameFilled) null else ATTENDEES_NOT_FILLED_MESSAGE
        _viewState.setValue(AddEventViewState.ShowError(errorMessage))
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
        _viewState.setValue(AddEventViewState.AddEventButtonVisibility(isAllFieldsFilled()))
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
    }
}