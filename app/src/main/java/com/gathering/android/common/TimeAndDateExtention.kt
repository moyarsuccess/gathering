package com.gathering.android.common

import java.util.Calendar

fun Calendar.getYear(): Int {
    return get(Calendar.YEAR)
}

fun Calendar.getMonth(): Int {
    return get(Calendar.MONTH)
}

fun Calendar.getDay(): Int {
    return get(Calendar.DAY_OF_MONTH)
}

fun Calendar.getHour(): Int {
    return get(Calendar.HOUR_OF_DAY)
}

fun Calendar.getMinute(): Int {
    return get(Calendar.MINUTE)
}