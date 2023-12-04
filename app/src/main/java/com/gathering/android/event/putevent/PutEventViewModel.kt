package com.gathering.android.event.putevent

import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.SEPARATOR
import com.gathering.android.common.getDay
import com.gathering.android.common.getHour
import com.gathering.android.common.getMinute
import com.gathering.android.common.getMonth
import com.gathering.android.common.getYear
import com.gathering.android.event.Event
import com.gathering.android.event.model.AttendeeModel
import com.gathering.android.event.model.EventLocation
import com.gathering.android.event.putevent.repo.PutEventModel
import com.gathering.android.event.repo.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


class PutEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private var geocoder: Geocoder,
) : ViewModel() {

    private var putEventNavigator: PutEventNavigator? = null

    private enum class StateMode {
        EDIT,
        ADD,
    }

    private data class EventViewModelState(
        val eventId: Long? = 0,
        val imageUri: String? = null,
        val eventName: String? = null,
        val eventDescription: String? = null,
        val errorMessage: String? = null,
        val showProgress: Boolean? = false,
        val year: Int = 0,
        val month: Int = 0,
        val day: Int = 0,
        val hour: Int = 0,
        val minute: Int = 0,
        val lat: Double? = null,
        val lon: Double? = null,
        val eventAttendeeModels: List<AttendeeModel>? = null,
        val actionButtonText: String? = null,
        val actionButtonEnable: Boolean? = false,
        val stateMode: StateMode = StateMode.ADD,
    ) {

        fun getEventLocation(): EventLocation {
            return EventLocation(lat, lon)
        }

        fun getDate(): Long {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            return cal.time.time
        }

        fun getFormattedDate(): String? {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return simpleDateFormat.format(cal.time)
        }

        fun getFormattedTime(): String? {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return simpleDateFormat.format(cal.time)
        }

    }

    private val viewModelState = MutableStateFlow(EventViewModelState())
    val uiState: StateFlow<PutEventUiState> = viewModelState.map { viewModelState ->
        PutEventUiState(
            imageUri = viewModelState.imageUri,
            eventName = viewModelState.eventName,
            eventDescription = viewModelState.eventDescription,
            eventDate = viewModelState.getFormattedDate(),
            eventTime = viewModelState.getFormattedTime(),
            eventAddress = EventLocation(
                viewModelState.lat ?: 0.0,
                viewModelState.lon ?: 0.0
            ).addressFromLocation(),
            eventAttendees = viewModelState.eventAttendeeModels.toCommaSeparatedString(),
            actionButtonText = viewModelState.actionButtonText,
            actionButtonEnable = viewModelState.actionButtonEnable
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PutEventUiState()
    )

    fun onViewCreated(event: Event?, putEventNavigator: PutEventNavigator) {
        this.putEventNavigator = putEventNavigator
        val stateMode = if (event == null) StateMode.ADD else StateMode.EDIT
        update { currentState ->
            val cal = Calendar.getInstance().apply {
                event?.dateAndTime?.also { time = Date(it) }
            }
            currentState.copy(
                eventId = event?.eventId ?: 0,
                imageUri = event?.photoUrl,
                eventName = event?.eventName,
                eventDescription = event?.description,
                year = cal.getYear(),
                month = cal.getMonth(),
                day = cal.getDay(),
                hour = cal.getHour(),
                minute = cal.getMinute(),
                lat = event?.latitude,
                lon = event?.longitude,
                eventAttendeeModels = event?.attendeeModels,
                actionButtonText = if (event == null) "Add" else "Save",
                stateMode = stateMode,
            )
        }
    }

    fun onImageButtonClicked() {
        putEventNavigator?.navigateToImagePicker()
    }

    fun onImageSelected(imageUrl: String?) {
        if (imageUrl == null) return
        update { currentState ->
            currentState.copy(
                imageUri = imageUrl
            )
        }
    }

    fun onDateButtonClicked() {
        viewModelState.value.apply {
            val cal = Calendar.getInstance()
            val y: Int = if (year == 0) cal.getYear() else year
            val m: Int = if (month == 0) cal.getMonth() else month
            val d: Int = if (day == 0) cal.getDay() else day
            println("WTF - $y $m $d")
            putEventNavigator?.navigateToDatePicker(
                year = y,
                month = m,
                day = d,
            )
        }
    }

    fun onNewDateSelected(year: Int, month: Int, day: Int) {
        update { currentState ->
            currentState.copy(
                year = year,
                month = month,
                day = day,
            )
        }
    }

    fun onTimeButtonClicked() {
        putEventNavigator?.navigateToTimePicker(
            hour = viewModelState.value.hour,
            minute = viewModelState.value.minute,
        )
    }

    fun onNewTimeSelected(hour: Int, minute: Int) {
        update { currentState ->
            currentState.copy(
                hour = hour,
                minute = minute
            )
        }
    }

    fun onLocationButtonClicked() {
        putEventNavigator?.navigateToLocationPicker(viewModelState.value.getEventLocation())
    }

    fun onNewLocationSelected(address: String) {
        val location = address.locationFromAddressLine()
        update { currentState ->
            currentState.copy(
                lat = location.lat,
                lon = location.lon
            )
        }
    }

    fun onAttendeeButtonClicked() {
        putEventNavigator?.navigateToAttendeesPicker(
            viewModelState.value.eventAttendeeModels.toCommaSeparatedString()
        )
    }

    fun onNewAttendeeListSelected(attendeesEmails: List<String>) {
        update { currentState ->
            currentState.copy(
                eventAttendeeModels = attendeesEmails.map { attendeeEmail -> AttendeeModel(email = attendeeEmail) },
            )
        }
    }

    fun onEventNameChanged(eventName: String) {
        update { currentState ->
            currentState.copy(eventName = eventName)
        }
    }

    fun onDescriptionChanged(description: String) {
        update { currentState ->
            currentState.copy(eventDescription = description)
        }
    }

    fun onActionButtonClicked() {
        update { currentState ->
            currentState.copy(showProgress = true)
        }
        viewModelScope.launch {
            when (viewModelState.value.stateMode) {
                StateMode.EDIT -> {
                    eventRepository.editEvent(createPutEventModelFromCurrentState())
                    dismissPutEvent()
                }

                StateMode.ADD -> {
                    eventRepository.addEvent(createPutEventModelFromCurrentState())
                    dismissPutEvent()
                }
            }
        }
    }
    
    private fun dismissPutEvent() {
        putEventNavigator?.dismissPutEvent()
    }

    private fun update(stateUpdater: (currentState: EventViewModelState) -> EventViewModelState) {
        viewModelState.update { currentState ->
            val newState = stateUpdater(currentState)
            val isReady = newState.isStateReadyToAction()
            val copy = newState.copy(actionButtonEnable = isReady)
            copy
        }
    }

    private fun createPutEventModelFromCurrentState(): PutEventModel {
        val eventLocation = viewModelState.value.getEventLocation()
        return PutEventModel(
            eventId = viewModelState.value.eventId ?: 0,
            eventName = viewModelState.value.eventName ?: "",
            description = viewModelState.value.eventDescription ?: "",
            photoUri = viewModelState.value.imageUri ?: "",
            lat = eventLocation.lat ?: 0.0,
            lon = eventLocation.lon ?: 0.0,
            dateAndTime = viewModelState.value.getDate(),
            attendees = viewModelState.value.eventAttendeeModels?.map { it.email ?: "" }
                ?: emptyList()
        )
    }

    private fun EventViewModelState.isStateReadyToAction(): Boolean {
        if (eventName.isNullOrEmpty()) return false
        if (eventDescription.isNullOrEmpty()) return false
        if (eventAttendeeModels.isNullOrEmpty()) return false
        if (year == 0) return false
        if (lat == 0.0) return false
        if (lon == 0.0) return false
        if (imageUri.isNullOrEmpty()) return false
        return true
    }

    @Suppress("DEPRECATION")
    private suspend fun EventLocation.addressFromLocation(): String =
        withContext(Dispatchers.IO) {
            return@withContext geocoder.getFromLocation(
                lat ?: 0.0,
                lon ?: 0.0,
                1
            )?.getOrNull(0)?.getAddressLine(0) ?: ""
        }

    private fun String.locationFromAddressLine(): EventLocation {
        val addressList = geocoder
            .getFromLocationName(this, 1)
            ?: return EventLocation()

        val lat = addressList.firstOrNull()?.latitude ?: 0.0
        val long = addressList.firstOrNull()?.longitude ?: 0.0
        return EventLocation(lat, long)
    }

    companion object {
        private const val EVENT_REQUEST_FAILED = "EVENT_REQUEST_FAILED"

        private fun List<AttendeeModel>?.toCommaSeparatedString(): String {
            return this?.joinToString(SEPARATOR) { it.email ?: "" } ?: ""
        }
    }
}