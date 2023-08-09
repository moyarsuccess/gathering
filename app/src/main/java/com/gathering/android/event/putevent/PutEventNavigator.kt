package com.gathering.android.event.putevent

interface PutEventNavigator {

    fun navigateToAddressSelector()

    fun navigateToDateSelector(latestYear: Int, latestMonth: Int, latestDay: Int)

    fun navigateToTimeSelector(latestHour: Int, latestMinute: Int)

    fun navigateImageSelector()

    fun dismissPutEvent()

    fun navigateToAttendeesSelector(attendees: List<String>)
}