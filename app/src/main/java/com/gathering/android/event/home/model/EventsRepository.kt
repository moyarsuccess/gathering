package com.gathering.android.event.home.model

import com.gathering.android.event.model.Event
import com.gathering.android.event.model.EventLocation
import com.gathering.android.event.model.User
import javax.inject.Inject

class EventsRepository @Inject constructor() {

    fun provideEventListMock(): List<Event> {
        return mutableListOf(
            Event(
                eventName = "anahid's Party",
                host = User("1", "anahid", "animansoubi@gmail.com"),
                description = "Dinner and game is waiting for you",
                photoUrl = "",
                location = EventLocation(
                    45.5019, 73.5674, "Anahid's Home in Montreal"
                ),
                dateAndTime = 0,
                isContactEvent = false,
                eventCost = 10
            ), Event(
                eventName = "Ida's Party",
                host = User("2", "Ida", "idaoskooei@gmail.com"),
                description = "Dinner and game and swimming pool is waiting for you",
                photoUrl = "",
                location = EventLocation(
                    43.0896, 79.0849, "Niagra falls"
                ),
                dateAndTime = 0,
                isContactEvent = true,
                isMyEvent = true,
                eventCost = 10
            ), Event(
                eventName = "Amir's Party",
                host = User("3", "Amir", "amir.ziarati.n@gmail.com"),
                description = "Dinner and game and swimming pool is waiting for you",
                photoUrl = "",
                location = EventLocation(
                    43.0896, 79.0849, "Niagra falls"
                ),
                dateAndTime = 0,
                isContactEvent = true,
                eventCost = 10,
            ), Event(
                dateAndTime = 0,
                eventName = "Mo's Party",
                host = User("4", "MO", "moradyar.mohammad@gmail.com"),
                description = "Dinner and game and swimming pool is waiting for you",
                photoUrl = "",
                location = EventLocation(
                    49.2827, 123.1207, "Mo's Vancouver Villa"
                ),
                isContactEvent = true,
                eventCost = 10,
            )
        )
    }
}
