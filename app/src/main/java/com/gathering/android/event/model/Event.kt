package com.gathering.android.event.model

import com.gathering.android.auth.model.User
import com.google.gson.Gson
import java.io.Serializable

data class Event(
    val eventName: String,
    val host: User?,
    val description: String,
    val photoUrl: String,
    val location: EventLocation,
    val dateAndTime: Long,
    val isContactEvent: Boolean = false,
    val isMyEvent: Boolean = false,
    val attendees: List<String> = listOf(),
    val eventCost: Int = 0
) : Serializable {

    fun getAttendeesJson(): String {
        return Gson().toJson(attendees)
    }
}
