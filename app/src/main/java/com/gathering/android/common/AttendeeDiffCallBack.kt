package com.gathering.android.common

class AttendeeDiffCallBack(
    private val oldList: List<String>, private val newList: List<String>
) : DiffCallBack<String>(oldList, newList) {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}