package com.gathering.android.event.model

import java.io.Serializable

data class EventLocation(
    val lat: Double? = 0.0,
    val lon: Double? = 0.0,
) : Serializable


