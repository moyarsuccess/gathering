package com.gathering.android.event.putevent

interface PutEventNavigator {

    fun navigateToImagePicker()

    fun navigateToDatePicker(year: Int, month: Int, day: Int)

    fun navigateToTimePicker(hour: Int, minute: Int)

    fun navigateToLocationPicker()

    fun navigateToAttendeesPicker(attendees: String)

    fun dismissPutEvent()
}