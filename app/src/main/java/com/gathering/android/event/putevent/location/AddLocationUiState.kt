package com.gathering.android.event.putevent.location

import com.gathering.android.event.model.EventLocation

data class AddLocationUiState(
    val addressList: List<String> = listOf(),
    val selectedAddress: String? = null,
    val selectedLocation: EventLocation = EventLocation(0.0, 0.0),
    val errorMessage: String? = null,
    val okButtonEnable: Boolean? = false,
    val addMarker: Boolean = false,
    val setSelectedAddress: Boolean = false,
)