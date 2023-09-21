package com.gathering.android.common

import androidx.recyclerview.widget.DiffUtil
import com.gathering.android.event.Event

class EventsDiffCallback(
    private val oldList: List<Event>, private val newList: List<Event>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return oldList[oldItemPosition].eventId == newList[newItemPosition].eventId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
