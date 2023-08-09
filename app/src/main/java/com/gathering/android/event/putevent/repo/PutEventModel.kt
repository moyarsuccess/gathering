package com.gathering.android.event.putevent.repo

import com.google.gson.Gson

data class PutEventModel(
    val eventId: Long,
    val eventName: String,
    val description: String,
    val photoUri: String,
    val lat: Double,
    val lon: Double,
    val dateAndTime: Long,
    private val attendees: List<String>,
) {
    fun getAttendeesJson(): String {
        return Gson().toJson(attendees)
    }
}
