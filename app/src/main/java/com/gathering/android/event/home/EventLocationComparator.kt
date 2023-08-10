package com.gathering.android.event.home

import android.location.Location
import com.gathering.android.event.model.EventLocation
import com.gathering.android.event.model.EventModel
import javax.inject.Inject
import kotlin.math.*


class EventLocationComparator @Inject constructor() : Comparator<EventModel> {
    override fun compare(event1: EventModel, event2: EventModel): Int {
        val eventLocation1 = EventLocation(
            lat = event1.latitude,
            lon = event1.longitude,
        )
        val eventLocation2 = EventLocation(
            lat = event2.latitude,
            lon = event2.longitude,
        )
        val distA = eventLocation1.distanceTo(getCurrentLocation())
        val distB = eventLocation2.distanceTo(getCurrentLocation())

        return (distA).compareTo(distB)
    }

    private fun EventLocation.distanceTo(otherLocation: Location): Double {
        val lat1 = Math.toRadians(lat ?: 0.0)
        val lon1 = Math.toRadians(lon ?: 0.0)
        val lat2 = Math.toRadians(otherLocation.latitude)
        val lon2 = Math.toRadians(otherLocation.longitude)

        val dlat = lat2 - lat1
        val dlon = lon2 - lon1
        val a = sin(dlat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dlon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return 6371 * c
    }

    private fun getCurrentLocation(): Location {
        return Location("").also { it.altitude = 43.6532; it.longitude = 79.3832 }
    }
}

