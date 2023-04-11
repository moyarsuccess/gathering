package com.gathering.android.event.home.model

data class Event(
    val eventId: String = "",
    val eventName: String = "",
    val hostName: String = "",
    val description: String = "",
    val photoUrl: String = "",
    val location: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val date: String = "",
    //We can set list of activity or define different activities
    val activities: List<String> = listOf(),
    val eventCost: Int = 0
)
