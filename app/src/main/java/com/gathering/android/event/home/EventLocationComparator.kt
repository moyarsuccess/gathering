package com.gathering.android.event.home

import android.location.Location
import com.gathering.android.event.home.model.Event
import javax.inject.Inject


class EventLocationComparator @Inject constructor() : Comparator<Event> {
    override fun compare(event1: Event, event2: Event): Int {
        val distA = event1.location?.distanceTo(getCurrentLocation())
        val distB = event2.location?.distanceTo(getCurrentLocation())

        return (distA ?: 0f).compareTo(distB ?: 0f)
    }

    private fun getCurrentLocation(): Location {
        return Location("").also { it.altitude = 43.6532; it.longitude = 79.3832 }
    }
}

