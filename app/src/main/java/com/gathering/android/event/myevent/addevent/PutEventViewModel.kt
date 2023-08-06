package com.gathering.android.event.myevent.addevent

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.ResponseState
import com.gathering.android.event.Event
import com.gathering.android.event.model.Attendees
import com.gathering.android.event.myevent.addevent.repo.AddEventRepository
import com.gathering.android.event.toAttendeesString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class PutEventViewModel @Inject constructor(
    private val eventRepository: AddEventRepository
) : ViewModel() {

    private val viewState: MutableStateFlow<PutEventViewState> =
        MutableStateFlow(PutEventViewState.NotReadyToAction())
    val uiState: Flow<PutEventUiState> = viewState.map(PutEventViewState::toUiState).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PutEventUiState()
    )

    private var isImageFilled: Boolean = false
    private var isEventNameFilled: Boolean = false
    private var isDescriptionFilled: Boolean = false
    private var isDateFilled: Boolean = false
    private var isTimeFilled: Boolean = false
    private var isAddressFilled: Boolean = false
    private var isAttendeeListFilled: Boolean = false

    private val attendeesEmailList = mutableListOf<String>()

    fun onViewCreated(event: Event?) {
        viewState.update {
            if (event == null) {
                PutEventViewState.NotReadyToAction()
            } else {
                val cal = Calendar.getInstance()
                cal.time = Date(event.dateAndTime)
                PutEventViewState.ReadyToAction(
                    id = event.eventId,
                    name = event.eventName,
                    description = event.description,
                    phonePhotoUrl = null,
                    networkPhotoUrl = event.photoUrl,
                    address = event.location.addressLine ?: "",
                    year = cal.get(Calendar.YEAR),
                    month = cal.get(Calendar.MONTH),
                    day = cal.get(Calendar.DAY_OF_MONTH),
                    hour = cal.get(Calendar.HOUR_OF_DAY),
                    minute = cal.get(Calendar.MINUTE),
                    attendees = event.attendees
                )
            }

        }
    }

    fun onImageButtonClicked() {
        // TODO navigate to select image fragment
    }

    fun onImageSelected(imageUrl: String?) {
        if (imageUrl == null) {
            return
        }

        viewState.update { currentState ->
            when (currentState) {
                is PutEventViewState.NotReadyToAction -> {
                    val newState = currentState.copy(phonePhotoUrl = imageUrl)
                    if (newState.isReady()) {
                        currentState.toReadyToAction()
                    } else {
                        newState
                    }
                }

                is PutEventViewState.ReadyToAction -> {
                    val newState = currentState.copy(phonePhotoUrl = imageUrl)
                    if (newState.isReady()) {
                        currentState
                    } else {
                        currentState.toNotReadyToAction()
                    }
                }
            }
        }
    }

    fun onDateButtonClicked() {
        // TODO navigate to date picker dialog
    }

    fun onNewDateSelected(year: Int, month: Int, day: Int) {
        viewState.update { currentState ->
            when (currentState) {
                is PutEventViewState.NotReadyToAction -> {
                    val newState = currentState.copy(
                        year = year,
                        month = month,
                        day = day
                    )
                    if (newState.isReady()) {
                        currentState.toReadyToAction()
                    } else {
                        newState
                    }
                }

                is PutEventViewState.ReadyToAction -> {
                    val newState = currentState.copy(
                        year = year,
                        month = month,
                        day = day
                    )
                    if (newState.isReady()) {
                        currentState
                    } else {
                        currentState.toNotReadyToAction()
                    }
                }
            }
        }
    }

    fun onTimeButtonClicked() {
        viewState.setValue(PutEventViewState.OpenTimePickerDialog)
    }

    fun onTimeChanged(time: String) {
        isTimeFilled = isTimeFiled(time)
        val errorMessage = if (isEventNameFilled) null else TIME_NOT_FILLED_MESSAGE
        viewState.setValue(PutEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onLocationButtonClicked() {
        viewState.setValue(PutEventViewState.NavigateToPutLocation)
    }

    fun onAddressChanged(address: String) {
        viewState.setValue(PutEventViewState.SetAddress(address))
        isAddressFilled = isAddressFiled(address)
        val errorMessage = if (isEventNameFilled) null else ADDRESS_NOT_FILLED_MESSAGE
        viewState.setValue(PutEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onInvitationButtonClicked() {
        viewState.setValue(PutEventViewState.NavigateToInviteFriend(attendeesEmailList))
    }

    fun onAddEventButtonClicked(event: Event) {
        viewState.setValue(PutEventViewState.MorphPutEventButtonToProgress)
        eventRepository.addEvent(event) { eventRequest ->
            when (eventRequest) {
                is ResponseState.Failure -> {
                    viewState.setValue(PutEventViewState.ShowError(EVENT_REQUEST_FAILED))
                }

                is ResponseState.Success<String> -> {
                    viewState.setValue(PutEventViewState.RevertPutEventProgressToButton)
                    viewState.setValue(PutEventViewState.NavigateToMyEvent(event))
                }
            }
        }
    }

    fun onEventNameChanged(eventName: String) {
        isEventNameFilled = isEventNameFiled(eventName)
        val errorMessage = if (isEventNameFilled) null else EVENT_NAME_NOT_FILLED_MESSAGE
        viewState.setValue(PutEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onDescriptionChanged(description: String) {
        isDescriptionFilled = isDescriptionFiled(description)
        val errorMessage = if (isEventNameFilled) null else DESCRIPTION_NOT_FILLED_MESSAGE
        viewState.setValue(PutEventViewState.ShowError(errorMessage))
        checkAllFieldsReady()
    }

    fun onAttendeeListChanged(contacts: List<String>) {
        attendeesEmailList.clear()
        attendeesEmailList.addAll(contacts)
        val attendee = contacts.joinToString(",")
        viewState.setValue(PutEventViewState.SetAttendeeList(attendee))

        isAttendeeListFilled = isAttendeesFiled(attendee)
        val errorMessage = if (isEventNameFilled) null else ATTENDEES_NOT_FILLED_MESSAGE
        viewState.setValue(PutEventViewState.ShowError(errorMessage))
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
        viewState.setValue(PutEventViewState.PutEventButtonVisibility(isAllFieldsFilled()))
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

    private sealed interface PutEventViewState {

        val id: Long?
        val name: String?
        val description: String?
        val phonePhotoUrl: String?
        val networkPhotoUrl: String?
        val address: String?
        val year: Int?
        val month: Int?
        val day: Int?
        val hour: Int?
        val minute: Int?
        val attendees: List<Attendees>?
        val mode: Mode

        fun toUiState(): PutEventUiState

        fun isReady(): Boolean {
            if (name.isNullOrEmpty()) return false
            if (description.isNullOrEmpty()) return false
            if (phonePhotoUrl.isNullOrEmpty() && networkPhotoUrl.isNullOrEmpty()) return false
            if (address.isNullOrEmpty()) return false
            if (attendees.isNullOrEmpty()) return false
            if (getDate() == null) return false
            return true
        }

        data class NotReadyToAction(
            override val id: Long? = null,
            override val name: String? = null,
            override val description: String? = null,
            override val phonePhotoUrl: String? = null,
            override val networkPhotoUrl: String? = null,
            override val address: String? = null,
            override val year: Int? = 0,
            override val month: Int? = 0,
            override val day: Int? = 0,
            override val hour: Int? = 0,
            override val minute: Int? = 0,
            override val attendees: List<Attendees>? = null,
            override val mode: Mode = if (id == null) Mode.ADD else Mode.EDIT,
        ) : PutEventViewState {

            fun toReadyToAction(): ReadyToAction {
                return ReadyToAction(
                    id = id,
                    name = name,
                    description = description,
                    phonePhotoUrl = phonePhotoUrl,
                    networkPhotoUrl = networkPhotoUrl,
                    address = address,
                    year = year,
                    month = month,
                    day = day,
                    hour = hour,
                    minute = minute,
                    attendees = attendees,
                )
            }

            override fun toUiState(): PutEventUiState {
                return PutEventUiState(
                    phoneImageUri = phonePhotoUrl,
                    networkImageUri = networkPhotoUrl,
                    eventName = name ?: "",
                    eventDescription = description ?: "",
                    address = address ?: "",
                    attendees = attendees?.toAttendeesString() ?: "",
                    date = formattedDate() ?: "",
                    time = formattedTime() ?: "",
                    btnText = mode.value,
                    enableActionButton = false
                )
            }
        }

        data class ReadyToAction(
            override val id: Long? = null,
            override val name: String? = null,
            override val description: String? = null,
            override val phonePhotoUrl: String? = null,
            override val networkPhotoUrl: String? = null,
            override val address: String? = null,
            override val year: Int? = 0,
            override val month: Int? = 0,
            override val day: Int? = 0,
            override val hour: Int? = 0,
            override val minute: Int? = 0,
            override val attendees: List<Attendees>? = null,
            override val mode: Mode = if (id == null) Mode.ADD else Mode.EDIT,
        ) : PutEventViewState {

            fun toNotReadyToAction(): NotReadyToAction {
                return NotReadyToAction(
                    id = id,
                    name = name,
                    description = description,
                    phonePhotoUrl = phonePhotoUrl,
                    networkPhotoUrl = networkPhotoUrl,
                    address = address,
                    year = year,
                    month = month,
                    day = day,
                    hour = hour,
                    minute = minute,
                    attendees = attendees,
                )
            }

            override fun toUiState(): PutEventUiState {
                return PutEventUiState(
                    phoneImageUri = phonePhotoUrl,
                    networkImageUri = networkPhotoUrl,
                    eventName = name ?: "",
                    eventDescription = description ?: "",
                    address = address ?: "",
                    attendees = attendees?.toAttendeesString() ?: "",
                    date = formattedDate() ?: "",
                    time = formattedTime() ?: "",
                    btnText = mode.value,
                    enableActionButton = true
                )
            }
        }

        fun formattedDate(): String? {
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateFormat.format(getDate())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun formattedTime(): String? {
            return try {
                val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                dateFormat.format(getDate())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getDate(): Date? {
            if (year == null) return null
            if (month == null) return null
            if (day == null) return null
            if (hour == null) return null
            if (minute == null) return null
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year!!)
            cal.set(Calendar.MONTH, month!!)
            cal.set(Calendar.DAY_OF_MONTH, day!!)
            cal.set(Calendar.HOUR_OF_DAY, hour!!)
            cal.set(Calendar.MINUTE, minute!!)
            return cal.time
        }
    }

    private enum class Mode(val value: String) {
        EDIT("Edit"),
        ADD("Add"),
    }
}