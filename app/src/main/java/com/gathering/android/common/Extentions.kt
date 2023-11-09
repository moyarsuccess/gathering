package com.gathering.android.common

import android.location.Location
import com.gathering.android.event.model.EventLocation
import com.google.android.gms.maps.model.LatLng

fun String.toImageUrl(): String {
    if (this.startsWith(LOCAL_CONTENT_URL_PREFIX)) return this
    return "${BASE_URL}/${PHOTO}/$this"
}

fun EventLocation?.isNullOrInvalid() = this == null ||
        this.lat == 0.0 ||
        this.lon == 0.0 ||
        this.lat == null ||
        this.lon == null

fun EventLocation.toLatLng(): LatLng {
    return LatLng(
        lat ?: 0.0,
        lon ?: 0.0
    )
}

fun LatLng.toEventLocation(): EventLocation {
    return EventLocation(
        lat = latitude,
        lon = longitude
    )
}

fun Location.toEventLocation(): EventLocation {
    return EventLocation(
        lat = latitude,
        lon = longitude
    )
}