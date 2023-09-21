package com.gathering.android.home

import com.gathering.android.event.Event

interface HomeNavigator {

    fun navigateToEventDetail(event: Event)

    fun navigateToIntroScreen()
}