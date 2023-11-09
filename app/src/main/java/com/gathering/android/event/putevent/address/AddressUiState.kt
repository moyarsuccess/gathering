package com.gathering.android.event.putevent.address

import com.gathering.android.event.model.EventLocation

data class AddressUiState(
    val suggestedAddressList: List<String> = listOf(),
    val addressTextValue: String? = null,
    val markerPosition: EventLocation = EventLocation(0.0, 0.0),
    val errorMessage: String? = null,
    val okButtonEnable: Boolean? = false,
    val dismissAutoSuggestion: Boolean = false,
)