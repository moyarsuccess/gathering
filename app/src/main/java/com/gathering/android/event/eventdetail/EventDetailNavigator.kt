package com.gathering.android.event.eventdetail


import com.gathering.android.event.model.AttendeeModel

interface EventDetailNavigator {

    fun navigateToAttendeesDetail(attendeeModels: List<AttendeeModel>)
}