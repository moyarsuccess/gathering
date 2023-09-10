package com.gathering.android.event.eventdetail


import com.gathering.android.event.model.Attendee

interface EventDetailNavigator {

    fun navigateToAttendeesDetail(attendees: List<Attendee>)
}