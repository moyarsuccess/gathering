package com.gathering.android.event.eventdetail

//TODO should be changed to enum later

sealed class AcceptType(val type: String) {
    object Yes : AcceptType("COMING")
    object No : AcceptType("NOT_COMING")
    object Maybe : AcceptType("MAYBE")
}