package com.gathering.android.event.putevent

import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.ResponseState
import com.gathering.android.common.SEPARATOR
import com.gathering.android.event.Event
import com.gathering.android.event.model.Attendee
import com.gathering.android.event.model.EventLocation
import com.gathering.android.event.putevent.repo.PutEventModel
import com.gathering.android.event.putevent.repo.PutEventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


class PutEventViewModel @Inject constructor(
    private val eventRepository: PutEventRepository,
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
        val eventAttendees: List<Attendee>? = null,
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
            cal.set(Calendar.YEAR, year ?: 0)
            cal.set(Calendar.MONTH, month ?: 0)
            cal.set(Calendar.DAY_OF_MONTH, day ?: 0)
            val simpleDateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
            return simpleDateFormat.format(cal.time)
        }

        fun getFormattedTime(): String? {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, hour ?: 0)
            cal.set(Calendar.MINUTE, minute ?: 0)
            val simpleDateFormat = SimpleDateFormat("HH:MM", Locale.getDefault())
            return simpleDateFormat.format(cal.time)
        }

    }

    private val viewModelState = MutableStateFlow(EventViewModelState())
    val uiState: Flow<PutEventUiState> = viewModelState.map { viewModelState ->
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
            eventAttendees = viewModelState.eventAttendees.toCommaSeparatedString(),
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
            val cal = Calendar.getInstance()
            cal.time = Date(event?.dateAndTime ?: 0L)
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
                eventAttendees = event?.attendees,
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
        putEventNavigator?.navigateToLocationPicker()
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
            viewModelState.value.eventAttendees.toCommaSeparatedString()
        )
    }

    fun onNewAttendeeListSelected(attendeesEmails: List<String>) {
        update { currentState ->
            currentState.copy(
                eventAttendees = attendeesEmails.map { attendeeEmail -> Attendee(email = attendeeEmail) },
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
        when (viewModelState.value.stateMode) {
            StateMode.EDIT -> {
                eventRepository.editEvent(createPutEventModelFromCurrentState(), ::onResponseReady)
            }

            StateMode.ADD -> {
                eventRepository.addEvent(createPutEventModelFromCurrentState(), ::onResponseReady)
            }
        }
    }


    private fun onResponseReady(eventRequest: ResponseState<String>) {
        when (eventRequest) {
            is ResponseState.Failure -> {
                update { currentState ->
                    currentState.copy(errorMessage = EVENT_REQUEST_FAILED, showProgress = false)
                }
            }

            is ResponseState.Success<String> -> {
                putEventNavigator?.dismissPutEvent()
                update { currentState ->
                    currentState.copy(showProgress = false)
                }
            }
        }
    }

    private fun update(stateUpdater: (currentState: EventViewModelState) -> EventViewModelState) {
            viewModelState.update { currentState ->
                val newState = stateUpdater(currentState)
                val isReady = newState.isStateReadyToAction()
                val copy = newState.copy(
                    actionButtonEnable = isReady
                )
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
            attendees = viewModelState.value.eventAttendees?.map { it.email ?: "" } ?: emptyList()
        )
    }

    private fun EventViewModelState.isStateReadyToAction(): Boolean {
        if (eventName.isNullOrEmpty()) return false
        if (eventDescription.isNullOrEmpty()) return false
        if (eventAttendees.isNullOrEmpty()) return false
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
            )?.getOrNull(0)?.getAddressLine(0).toString()
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

        private fun Calendar.getYear(): Int {
            return get(Calendar.YEAR)
        }

        private fun Calendar.getMonth(): Int {
            return get(Calendar.MONTH)
        }

        private fun Calendar.getDay(): Int {
            return get(Calendar.DAY_OF_MONTH)
        }

        private fun Calendar.getHour(): Int {
            return get(Calendar.HOUR_OF_DAY)
        }

        private fun Calendar.getMinute(): Int {
            return get(Calendar.MINUTE)
        }

        private fun List<Attendee>?.toCommaSeparatedString(): String {
            return this?.joinToString(SEPARATOR) { it.email ?: "" } ?: ""
        }
    }
}