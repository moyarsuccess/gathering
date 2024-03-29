package com.gathering.android.event.eventdetail

import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.R
import com.gathering.android.auth.model.User
import com.gathering.android.common.UserRepository
import com.gathering.android.common.getDay
import com.gathering.android.common.getHour
import com.gathering.android.common.getMinute
import com.gathering.android.common.getMonth
import com.gathering.android.common.getYear
import com.gathering.android.event.Event
import com.gathering.android.event.GENERAL_ERROR
import com.gathering.android.event.SERVER_DID_NOT_CATCH_ATTENDANCE_RESPONSE
import com.gathering.android.event.eventdetail.acceptrepo.AttendanceStateRepository
import com.gathering.android.event.model.AttendeeModel
import com.gathering.android.event.model.EventLocation
import com.gathering.android.event.repo.EventException
import com.gathering.android.event.repo.EventRepository
import com.gathering.android.event.toEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class EventDetailViewModel @Inject constructor(
    private val attendanceStateRepo: AttendanceStateRepository,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private var geocoder: Geocoder,
) : ViewModel() {

    private var eventDetailNavigator: EventDetailNavigator? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is EventException -> {
                when (throwable) {
                    EventException.NotAbleToCatchAttendanceResponseException -> SERVER_DID_NOT_CATCH_ATTENDANCE_RESPONSE
                    else -> {
                        GENERAL_ERROR
                    }
                }
            }
            else -> {
                GENERAL_ERROR
            }
        }
        viewModelState.update { currentState ->
            currentState.copy(
                errorMessage = errorMessage
            )
        }
    }

    private val viewModelState = MutableStateFlow(EventDetailViewModelState())
    val uiState: StateFlow<EventDetailUiState> = viewModelState.map { viewModelState ->
        val acceptButtonBackColor = viewModelState.getYesBackColor()
        val declineButtonBackColor = viewModelState.getNoBackColor()
        val maybeButtonBackColor = viewModelState.getMaybeBackColor()
        EventDetailUiState(
            eventId = viewModelState.eventId,
            imageUri = viewModelState.imageUri,
            eventName = viewModelState.eventName,
            hostEvent = viewModelState.eventHostEmail,
            eventDescription = viewModelState.eventDescription,
            eventDate = viewModelState.getFormattedDate(),
            eventTime = viewModelState.getFormattedTime(),
            eventAddress = viewModelState.getAddress(geocoder),
            acceptButtonBackColor = acceptButtonBackColor,
            declineButtonBackColor = declineButtonBackColor,
            maybeButtonBackColor = maybeButtonBackColor,
            acceptType = viewModelState.acceptType,
            errorMessage = viewModelState.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = EventDetailUiState()
    )

    fun onViewCreated(eventId: Long?, eventDetailNavigator: EventDetailNavigator) {
        this.eventDetailNavigator = eventDetailNavigator

        viewModelScope.launch(exceptionHandler) {
            val event = try {
                eventRepository.getEventById(eventId ?: 0).toEvent()
            } catch (e: Exception) {
                viewModelState.update { currentState ->
                    currentState.copy(errorMessage = SERVER_ERROR)
                }
                Log.d("XXX:", e.toString())
                return@launch
            }

            viewModelState.update { currentState ->
                val cal = Calendar.getInstance().apply {
                    event.dateAndTime.also { time = Date(it) }
                }
                val acceptType = userRepository.getUser().obtainAttendeeAcceptType(event)
                currentState.copy(
                    eventId = event.eventId,
                    imageUri = event.photoUrl,
                    eventName = event.eventName,
                    eventHostEmail = event.eventHostEmail,
                    eventDescription = event.description,
                    lat = event.latitude,
                    lon = event.longitude,
                    eventAttendeeModels = event.attendeeModels,
                    acceptType = acceptType,
                    year = cal.getYear(),
                    month = cal.getMonth(),
                    day = cal.getDay(),
                    hour = cal.getHour(),
                    minute = cal.getMinute(),
                )
            }
        }

    }

    fun onYesButtonClicked() {
        requestAcceptTypeChange(AcceptType.Yes)
    }

    fun onNoButtonClicked() {
        requestAcceptTypeChange(AcceptType.No)
    }

    fun onMaybeButtonClicked() {
        requestAcceptTypeChange(AcceptType.Maybe)
    }

    fun onTvAttendeesDetailsClicked() {
        eventDetailNavigator?.navigateToAttendeesDetail(
            viewModelState.value.eventAttendeeModels ?: emptyList()
        )
    }

    private fun requestAcceptTypeChange(acceptType: AcceptType) {
        viewModelScope.launch(exceptionHandler) {
            attendanceStateRepo.setEventAcceptType(
                eventId = viewModelState.value.eventId ?: 0,
                accept = acceptType
            )
            viewModelState.update { currentState ->
                currentState.copy(acceptType = acceptType)
            }
        }
    }

    private fun User?.obtainAttendeeAcceptType(event: Event?): AcceptType {
        val attendee = event?.attendeeModels?.firstOrNull { attendee ->
            attendee.email == this?.email
        }
        return when (attendee?.accepted) {
            COMING -> AcceptType.Yes
            NOT_COMING -> AcceptType.No
            MAYBE -> AcceptType.Maybe
            else -> AcceptType.No
        }
    }

    private data class EventDetailViewModelState(
        val currentUser: User? = User(),
        val imageUri: String? = null,
        val eventId: Long? = null,
        val eventName: String? = null,
        val eventHostEmail: String? = null,
        val eventDescription: String? = null,
        val lat: Double? = null,
        val lon: Double? = null,
        val day: Int = 0,
        val month: Int = 0,
        val year: Int = 0,
        val hour: Int = 0,
        val minute: Int = 0,
        val eventAttendeeModels: List<AttendeeModel>? = null,
        val acceptType: AcceptType = AcceptType.No,
        val errorMessage: String? = null,
    ) {
        fun getFormattedDate(): String? {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year ?: 0)
            cal.set(Calendar.MONTH, month ?: 0)
            cal.set(Calendar.DAY_OF_MONTH, day ?: 0)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return simpleDateFormat.format(cal.time)
        }

        fun getFormattedTime(): String? {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, hour ?: 0)
            cal.set(Calendar.MINUTE, minute ?: 0)
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return simpleDateFormat.format(cal.time)
        }

        fun getYesBackColor(): Int {
            return if (acceptType == AcceptType.Yes) return R.color.gray else R.drawable.custom_button
        }

        fun getNoBackColor(): Int {
            return if (acceptType == AcceptType.No) return R.color.gray else R.drawable.custom_button
        }

        fun getMaybeBackColor(): Int {
            return if (acceptType == AcceptType.Maybe) return R.color.gray else R.drawable.custom_button
        }

        suspend fun getAddress(geocoder: Geocoder): String {
            return EventLocation(
                lat ?: 0.0,
                lon ?: 0.0
            ).addressFromLocation(geocoder)
        }

        @Suppress("DEPRECATION")
        private suspend fun EventLocation.addressFromLocation(geocoder: Geocoder): String =
            withContext(Dispatchers.IO) {
                return@withContext geocoder.getFromLocation(
                    lat ?: 0.0,
                    lon ?: 0.0,
                    1
                )?.getOrNull(0)?.getAddressLine(0).toString()
            }
    }

    companion object {
        private const val COMING = "COMING"
        private const val NOT_COMING = "NOT_COMING"
        private const val MAYBE = "MAYBE"
        private const val SERVER_ERROR = "The event is not available now :("
    }
}
