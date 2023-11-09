package com.gathering.android.utils.location.geocoder

import android.location.Geocoder
import com.gathering.android.event.model.EventLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeocoderWrapperImpl(
    private val geocoder: Geocoder
) : GeocoderWrapper {
    override suspend fun addressFromLocation(eventLocation: EventLocation) =
        withContext(Dispatchers.IO) {
            @Suppress("DEPRECATION")
            return@withContext try {
                geocoder.getFromLocation(
                    eventLocation.lat ?: 0.0,
                    eventLocation.lon ?: 0.0,
                    1
                )?.getOrNull(0)?.getAddressLine(0).toString()
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

    override suspend fun locationFromAddress(address: String) = withContext(Dispatchers.IO) {
        return@withContext try {
            @Suppress("DEPRECATION") val addressList = geocoder.getFromLocationName(address, 1)
                ?: return@withContext EventLocation()

            val lat = addressList.firstOrNull()?.latitude ?: 0.0
            val long = addressList.firstOrNull()?.longitude ?: 0.0
            EventLocation(lat, long)
        } catch (e: Exception) {
            e.printStackTrace()
            EventLocation()
        }
    }
}