package com.gathering.android.common

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import java.lang.reflect.Modifier.PRIVATE
import java.util.LinkedList
import java.util.Queue

class ActiveMutableLiveData<T> : MutableLiveData<T>() {

    @VisibleForTesting(otherwise = PRIVATE)
    val values: Queue<T> = LinkedList()

    private var isActive: Boolean = false

    override fun onActive() {
        isActive = true
        while (values.isNotEmpty()) {
            values.poll()?.let { setValue(it) }
        }
    }

    override fun onInactive() {
        isActive = false
    }

    override fun setValue(value: T) {
        if (isActive) {
            super.setValue(value)
        } else {
            values.add(value)
        }
    }
}