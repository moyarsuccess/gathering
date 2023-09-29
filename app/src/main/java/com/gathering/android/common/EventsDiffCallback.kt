package com.gathering.android.common

import com.gathering.android.event.Event

class EventsDiffCallback(
    private val oldList: List<Event>, private val newList: List<Event>
) : DiffCallBack<Event>(oldList, newList) {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].eventId == newList[newItemPosition].eventId
    }
}
