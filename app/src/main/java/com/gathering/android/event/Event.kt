package com.gathering.android.event

import com.gathering.android.event.model.EventLocation
import com.google.gson.Gson
import java.io.Serializable

data class Event(
    val eventName: String = "",
    val eventHostEmail: String = "",
    val description: String = "",
    val photoUrl: String = "",
    val location: EventLocation,
    val dateAndTime: Long = 0,
    val isContactEvent: Boolean = false,
    val isMyEvent: Boolean = false,
    val attendees: List<String> = listOf(),
    val eventCost: Int = 0
) : Serializable {

    fun getAttendeesJson(): String {
        return Gson().toJson(attendees)
    }
}

