package com.gathering.android.home

import com.gathering.android.event.model.EventModel
import javax.inject.Inject

class EventDateComparator @Inject constructor() : Comparator<EventModel> {

    override fun compare(event1: EventModel?, event2: EventModel?): Int {
        val date1 = event1?.dateTime ?: 0
        val date2 = event2?.dateTime ?: 0

        return date1.compareTo(date2)
    }
}