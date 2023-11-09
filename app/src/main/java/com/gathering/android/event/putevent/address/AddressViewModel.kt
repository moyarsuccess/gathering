package com.gathering.android.event.putevent.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.isNullOrInvalid
import com.gathering.android.common.toEventLocation
import com.gathering.android.event.model.EventLocation
import com.gathering.android.utils.location.LocationHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddressViewModel @Inject constructor(
    private val locationHelper: LocationHelper
) : ViewModel() {

    private var addressNavigator: AddressNavigator? = null

    private val viewModelState = MutableStateFlow(AddLocationViewModelState())
    val uiState: StateFlow<AddressUiState> = viewModelState.map { viewModelState ->
        AddressUiState(
            suggestedAddressList = viewModelState.suggestedAddressList,
            addressTextValue = viewModelState.addressTextValue,
            markerPosition = viewModelState.markerPosition,
            errorMessage = viewModelState.errorMessage,
            okButtonEnable = viewModelState.okButtonEnable,
            dismissAutoSuggestion = viewModelState.dismissAutoSuggestion,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AddressUiState()
    )

    fun onViewCreated(
        eventLocation: EventLocation? = null,
        addressNavigator: AddressNavigator
    ) {
        this.addressNavigator = addressNavigator
        if (!eventLocation.isNullOrInvalid()) {
            viewModelScope.launch {
                viewModelState.update { currentState ->
                    currentState.copy(
                        addressTextValue = locationHelper.addressFromLocation(eventLocation!!),
                        markerPosition = eventLocation,
                        okButtonEnable = false,
                    )
                }
            }
            return
        } else {
            viewModelScope.launch {
                val currentLocation = locationHelper.getCurrentLocation()
                viewModelState.update { currentState ->
                    currentState.copy(
                        addressTextValue = locationHelper.addressFromLocation(currentLocation),
                        markerPosition = currentLocation,
                        okButtonEnable = false,
                    )
                }
            }
        }
    }

    fun onAddressChanged(address: String) {
        viewModelState.update { currentState ->
            currentState.copy(
                addressTextValue = address,
            )
        }
        if (address.length < AUTO_SUGGESTION_THRESH_HOLD) return
        viewModelScope.launch {
            val addresses = locationHelper.suggestAddressList(address)
            if (addresses.isEmpty()) return@launch
            viewModelState.update { currentState ->
                currentState.copy(
                    suggestedAddressList = addresses,
                    okButtonEnable = true,
                    dismissAutoSuggestion = false,
                )
            }
        }
    }

    fun onClearClicked() {
        viewModelState.update { currentState ->
            currentState.copy(addressTextValue = "")
        }
    }

    fun onSuggestedAddressClicked(suggestedAddress: String) {
        viewModelScope.launch {
            locationHelper.locationFromAddress(suggestedAddress).also { location ->
                viewModelState.update { currentState ->
                    currentState.copy(
                        markerPosition = location,
                        addressTextValue = suggestedAddress,
                        dismissAutoSuggestion = true,
                    )
                }
            }
        }
    }

    fun onOKButtonClicked() {
        addressNavigator?.navigateToAddEvent(viewModelState.value.addressTextValue ?: "")
    }

    fun onMapLongClicked(latLng: LatLng) {
        viewModelScope.launch {
            locationHelper.addressFromLocation(latLng.toEventLocation()).also { address ->
                viewModelState.update { currentState ->
                    currentState.copy(
                        addressTextValue = address,
                        okButtonEnable = true,
                        markerPosition = latLng.toEventLocation(),
                        dismissAutoSuggestion = true,
                    )
                }
            }
        }
    }

    fun onDismissed() {
        viewModelState.update { currentState ->
            currentState.copy(dismissAutoSuggestion = true)
        }
    }

    private data class AddLocationViewModelState(
        val suggestedAddressList: List<String> = listOf(),
        val addressTextValue: String? = null,
        val markerPosition: EventLocation = EventLocation(0.0, 0.0),
        val errorMessage: String? = null,
        val okButtonEnable: Boolean? = false,
        val dismissAutoSuggestion: Boolean = false,
    )

    companion object {
        private const val AUTO_SUGGESTION_THRESH_HOLD = 3
    }
}

