package com.gathering.android.event.putevent

import com.gathering.android.event.model.EventLocation

interface PutEventNavigator {

    fun navigateToImagePicker()

    fun navigateToDatePicker(year: Int, month: Int, day: Int)

    fun navigateToTimePicker(hour: Int, minute: Int)

    fun navigateToLocationPicker(eventLocation: EventLocation)

    fun navigateToAttendeesPicker(attendees: String)

    fun dismissPutEvent()
}