package com.gathering.android.event.putevent

import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.ResponseState
import com.gathering.android.common.SEPARATOR
import com.gathering.android.event.Event
import com.gathering.android.event.model.EventLocation
import com.gathering.android.event.putevent.repo.PutEventModel
import com.gathering.android.event.putevent.repo.PutEventRepository
import com.gathering.android.event.toAttendeesString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class PutEventViewModel @Inject constructor(
    private val eventRepository: PutEventRepository,
    private val geocoder: Geocoder
) : ViewModel() {

    private var putEventNavigator: PutEventNavigator? = null

    private enum class Mode {
        ADD,
        EDIT,
    }

    private data class EventInfoState(
        val eventId: Long? = null,
        val eventName: String? = null,
        val eventDescription: String? = null,
        val address: String? = null,
        val attendees: String? = null,
        val progressVisibility: Boolean? = null,
        val errorMessage: String? = null,
        val mode: Mode = Mode.ADD,
        val year: Int = Calendar.getInstance().getYear(),
        val month: Int = 0,
        val day: Int = 0,
        val hour: Int = 0,
        val minute: Int = 0,
        val actionButtonText: String? = null,
        val actionButtonVisibility: Boolean? = null,
        val phoneImageUrl: String? = null,
        val networkImageUrl: String? = null,
    ) {

        fun isDefaultDate(): Boolean {
            return year == 0 ||
                    month == 0 ||
                    day == 0
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

        fun formattedDate(): String? {
            if (isDefaultDate()) return null
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateFormat.format(getDate())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun formattedTime(): String? {
            if (isDefaultDate()) return null
            return try {
                val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                dateFormat.format(getDate())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private val eventInfoState: MutableStateFlow<EventInfoState> =
        MutableStateFlow(EventInfoState())
    val uiState: Flow<PutEventUiState> = eventInfoState.map { eventInfoState ->
        PutEventUiState(
            phoneImageUri = eventInfoState.phoneImageUrl,
            networkImageUri = eventInfoState.networkImageUrl,
            btnText = eventInfoState.actionButtonText ?: "",
            eventName = eventInfoState.eventName ?: "",
            eventDescription = eventInfoState.eventDescription ?: "",
            address = eventInfoState.address ?: "",
            attendees = eventInfoState.attendees ?: "",
            date = eventInfoState.formattedDate() ?: "",
            time = eventInfoState.formattedTime() ?: "",
            enableActionButton = eventInfoState.actionButtonVisibility ?: false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PutEventUiState()
    )

    private fun isStateReadyToAction(): Boolean {
        val currentState = eventInfoState.value
        if (currentState.eventName?.isEmpty() == true) return false
        if (currentState.eventDescription?.isEmpty() == true) return false
        if (currentState.address?.isEmpty() == true) return false
        if (currentState.year == 0) return false
        if (currentState.hour == 0) return false
        if (currentState.attendees?.isEmpty() == true) return false
        return true
    }

    fun onViewCreated(putEventNavigator: PutEventNavigator, event: Event?) {
        this.putEventNavigator = putEventNavigator
        update { currentState ->
            if (event == null) {
                return@update currentState.copy(
                    mode = Mode.ADD
                )
            }
            val cal = Calendar.getInstance()
            cal.time = Date(event.dateAndTime)
            currentState.copy(
                networkImageUrl = event.photoUrl, phoneImageUrl = null,
                eventName = event.eventName,
                eventDescription = event.description,
                address = event.location?.getAddressFromLocation() ?: "",
                attendees = event.attendees.toAttendeesString(),
                eventId = event.eventId,
                mode = Mode.EDIT,
                year = cal.getYear(),
                month = cal.getMonth(),
                day = cal.getDay(),
                hour = cal.getHour(),
                minute = cal.getMinute(),
            )
        }
    }

    fun onImageButtonClicked() {
        putEventNavigator?.navigateImageSelector()
    }

    fun onImageSelected(imageUrl: String?) {
        if (imageUrl == null) {
            return
        }
        update { currentState ->
            currentState.copy(phoneImageUrl = imageUrl)
        }
    }

    fun onDateButtonClicked() {
        eventInfoState.value.apply {
            val cal = Calendar.getInstance()
            val y: Int = if (year == 0) cal.getYear() else year
            val m: Int = if (month == 0) cal.getMonth() else month
            val d: Int = if (day == 0) cal.getDay() else day
            println("WTF - $y $m $d")
            putEventNavigator?.navigateToDateSelector(
                latestYear = y,
                latestMonth = m,
                latestDay = d,
            )
        }
    }

    fun onNewDateSelected(year: Int, month: Int, day: Int) {
        update { currentState ->
            currentState.copy(
                year = year,
                month = month,
                day = day
            )
        }
    }

    fun onTimeButtonClicked() {
        putEventNavigator?.navigateToTimeSelector(
            latestHour = eventInfoState.value.hour,
            latestMinute = eventInfoState.value.minute,
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

    fun onAddressButtonClicked() {
        putEventNavigator?.navigateToAddressSelector()
    }

    fun onAddressSelected(address: String) {
        update { currentState ->
            currentState.copy(address = address)
        }
    }

    fun onAttendeeButtonClicked() {
        putEventNavigator?.navigateToAttendeesSelector(
            eventInfoState.value.attendees?.split(SEPARATOR) ?: listOf()
        )
    }

    fun onAttendeeListSelected(attendees: List<String>) {
        update { currentState ->
            currentState.copy(
                attendees = attendees.joinToString(SEPARATOR) { it }
            )
        }
    }

    fun onActionButtonClicked() {
        update { currentState -> currentState.copy(progressVisibility = true) }
        when (eventInfoState.value.mode) {
            Mode.ADD -> eventRepository.addEvent(
                createPutEventModelFromCurrentState(),
                ::putEventHandler
            )

            Mode.EDIT -> eventRepository.editEvent(
                createPutEventModelFromCurrentState(),
                ::putEventHandler
            )
        }
    }

    private fun putEventHandler(responseState: ResponseState<String>) {
        when (responseState) {
            is ResponseState.Failure -> {
                update { currentState ->
                    currentState.copy(
                        errorMessage = EVENT_REQUEST_FAILED,
                        progressVisibility = false
                    )
                }
            }

            is ResponseState.Success<String> -> {
                update { currentState -> currentState.copy(progressVisibility = false) }
                putEventNavigator?.dismissPutEvent()
            }
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

    private fun update(stateUpdater: suspend (eventInfoState: EventInfoState) -> EventInfoState) {
        viewModelScope.launch {
            this@PutEventViewModel.eventInfoState.update { currentState ->
                val newEventInfoState = stateUpdater(currentState)
                val readyToAction = isStateReadyToAction()
                val buttonText = if (newEventInfoState.mode == Mode.EDIT) "Edit" else "Add"
                newEventInfoState.copy(
                    actionButtonText = buttonText,
                    actionButtonVisibility = readyToAction
                )
            }
        }
    }

    private fun createPutEventModelFromCurrentState(): PutEventModel {
        return PutEventModel(
            eventId = eventInfoState.value.eventId ?: 0,
            eventName = eventInfoState.value.eventName ?: "",
            description = eventInfoState.value.eventDescription ?: "",
            photoUri = eventInfoState.value.phoneImageUrl ?: "",
            lat = eventInfoState.value.address?.locationFromAddressLine()?.lat ?: 0.0,
            lon = eventInfoState.value.address?.locationFromAddressLine()?.lon ?: 0.0,
            dateAndTime = eventInfoState.value.getDate(),
            attendees = eventInfoState.value.attendees?.split(SEPARATOR) ?: listOf()
        )
    }

    @Suppress("DEPRECATION")
    private fun String.locationFromAddressLine(): EventLocation {
        val addressList = geocoder
            .getFromLocationName(this, 1)
            ?: return EventLocation()

        val lat = addressList.firstOrNull()?.latitude ?: 0.0
        val long = addressList.firstOrNull()?.longitude ?: 0.0
        return EventLocation(lat, long)
    }

    @Suppress("DEPRECATION")
    private suspend fun EventLocation.getAddressFromLocation(): String =
        withContext(Dispatchers.IO) {
            return@withContext geocoder.getFromLocation(
                lat ?: 0.0,
                lon ?: 0.0,
                1
            )?.getOrNull(0)?.getAddressLine(0).toString()
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
    }
}
