package com.gathering.android.utils.location

import com.gathering.android.event.model.EventLocation
import com.gathering.android.utils.location.fused.FusedLocationWrapper
import com.gathering.android.utils.location.geocoder.GeocoderWrapper
import com.gathering.android.utils.location.places.PlaceApiWrapper

class LocationHelperImpl(
    private val geocoderWrapper: GeocoderWrapper,
    private val placeApiWrapper: PlaceApiWrapper,
    private val fusedLocationWrapper: FusedLocationWrapper,
) : LocationHelper {
    override suspend fun addressFromLocation(eventLocation: EventLocation): String {
        return geocoderWrapper.addressFromLocation(eventLocation)
    }

    override suspend fun locationFromAddress(address: String): EventLocation {
        return geocoderWrapper.locationFromAddress(address)
    }

    override suspend fun getCurrentLocation(): EventLocation {
        return fusedLocationWrapper.getCurrentLocation()
    }

    override suspend fun suggestAddressList(address: String): List<String> {
        return placeApiWrapper.suggestAddressList(address)
    }
}