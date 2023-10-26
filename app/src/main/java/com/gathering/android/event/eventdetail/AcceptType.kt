package com.gathering.android.event.eventdetail

//TODO should be changed to enum later

sealed class AcceptType(val type: String) {
    data object Yes : AcceptType("COMING")
    data object No : AcceptType("NOT_COMING")
    data object Maybe : AcceptType("MAYBE")
}