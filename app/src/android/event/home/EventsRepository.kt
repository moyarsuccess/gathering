package com.gathering.android.event.home

import android.location.Location
import com.gathering.android.event.home.model.Event
import java.util.*
import javax.inject.Inject

class EventsRepository @Inject constructor() {

    fun provideEventListMock(): List<Event> {
        return mutableListOf(

            Event(
                eventId = "1",
                eventName = "anahid's Party",
                hostName = "Anahid",
                description = "Dinner and game is waiting for you",
                photoUrl = "",
                locationName = "Anahid's Home in Montreal",
                location = Location("").also { it.altitude = 45.5019; it.longitude = 73.5674 },
                startTime = "7:00",
                endTime = "midnight",
                date = Calendar.getInstance().time,
                isContactEvent = false,
                activities = listOf("Game", "Dinner", "Tea Time"),
                eventCost = 10
            ), Event(
                eventId = "2",
                eventName = "Ida's Party",
                hostName = "Ida",
                description = "Dinner and game and swimming pool is waiting for you",
                photoUrl = "",
                locationName = "Niagra falls",
                location = Location("").also { it.altitude = 43.0896; it.longitude = 79.0849 },
                startTime = "8:00",
                endTime = "12:00",
                date = Calendar.getInstance().time,
                isContactEvent = true,
                isMyEvent = true,
                activities = listOf("Game", "Dinner", "Tea Time"),
                eventCost = 10
            ), Event(
                eventId = "3",
                eventName = "Amir's Party",
                hostName = "Amir",
                description = "Dinner and game and swimming pool is waiting for you",
                photoUrl = "",
                locationName = "Niagra falls",
                location = Location("").also { it.altitude = 43.0896; it.longitude = 79.0849 },
                startTime = "8:00",
                endTime = "12:00",
                date = Calendar.getInstance().time,
                isContactEvent = true,
                activities = listOf("Game", "Dinner", "Tea Time"),
                eventCost = 10
            ), Event(
                eventId = "4",
                eventName = "Mo's Party",
                hostName = "Mo",
                description = "Dinner and game and swimming pool is waiting for you",
                photoUrl = "",
                locationName = "Mo's Vancouver Villa",
                location = Location("").also { it.altitude = 49.2827; it.longitude = 123.1207 },
                startTime = "8:00",
                endTime = "12:00",
                date = Calendar.getInstance().run {
                    add(Calendar.DAY_OF_YEAR, 1)
                    time
                },
                isContactEvent = true,
                activities = listOf("Game", "Dinner", "Tea Time"),
                eventCost = 10
            )
        )
    }
}
