package com.gathering.android.event.home.model

import android.location.Location
import java.io.Serializable
import java.util.Calendar
import java.util.Date

data class Event(
    val eventId: String = "",
    val eventName: String = "",
    val hostName: String = "",
    val description: String = "",
    val photoUrl: String = "",
    val locationName: String = "",
    val location: Location? = null,
    val time: String = "",
    val date: Date = Calendar.getInstance().time,
    val isContactEvent: Boolean = false,
    val isMyEvent: Boolean = false,
    //We can set list of activity or define different activities
    val activities: List<String> = listOf(),
    //We should have attendee object and set list of attendee instead of String
    val attendees: List<String> = listOf(),
    val eventCost: Int = 0
) : Serializable
