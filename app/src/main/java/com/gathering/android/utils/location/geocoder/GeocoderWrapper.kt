package com.gathering.android.utils.location.geocoder

import com.gathering.android.event.model.EventLocation

interface GeocoderWrapper {

    suspend fun addressFromLocation(eventLocation: EventLocation): String

    suspend fun locationFromAddress(address: String): EventLocation
}