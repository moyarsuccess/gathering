package com.gathering.android.event.myevent

//TODO should be changed to enum later

sealed class AcceptType(val type: String) {
    object Yes : AcceptType("Coming")
    object No : AcceptType("NotComing")
    object Maybe : AcceptType("Maybe")
}