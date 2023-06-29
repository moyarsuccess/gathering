package com.gathering.android.event.home

import com.gathering.android.event.model.Event
import javax.inject.Inject

class EventDateComparator @Inject constructor() : Comparator<Event> {

    override fun compare(event1: Event?, event2: Event?): Int {
        val date1 = event1?.dateAndTime ?: 0
        val date2 = event2?.dateAndTime ?: 0

        return date1.compareTo(date2)
    }
}