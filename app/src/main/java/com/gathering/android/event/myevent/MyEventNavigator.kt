package com.gathering.android.event.myevent

import com.gathering.android.event.Event

interface MyEventNavigator {

    fun navigateToAddEvent()

    fun navigateToEditEvent(event: Event)

    fun navigateToConfirmedAttendeesScreen(eventId: Long)

}