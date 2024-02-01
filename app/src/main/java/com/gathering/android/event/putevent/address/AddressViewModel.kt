package com.gathering.android.event.putevent.address

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.isNullOrInvalid
import com.gathering.android.common.toEventLocation
import com.gathering.android.event.GENERAL_ERROR
import com.gathering.android.event.LOCATION_IS_NULL_OR_INVALID
import com.gathering.android.event.model.EventLocation
import com.gathering.android.event.repo.EventException
import com.gathering.android.utils.location.LocationHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineExceptionHandler
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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var addressNavigator: AddressNavigator? = null


    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is EventException -> {
                when (throwable) {
                    AddressException.InvalidLocationException -> LOCATION_IS_NULL_OR_INVALID
                    is AddressException.GeneralException -> GENERAL_ERROR
                    else -> {
                        GENERAL_ERROR
                    }
                }
            }

            else -> {
                GENERAL_ERROR
            }
        }
        viewModelState.update { currentState ->
            currentState.copy(
                errorMessage = errorMessage,
            )
        }
    }

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
        viewModelScope.launch(exceptionHandler) {
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
                return@launch
            } else {
                viewModelScope.launch {
                    val currentLocation = locationHelper.getCurrentLocation()
                    viewModelState.update { currentState ->
                        currentState.copy(
                            addressTextValue = locationHelper.addressFromLocation(currentLocation),
                            markerPosition = currentLocation,
                            okButtonEnable = false,
                            errorMessage = LOCATION_IS_NULL_OR_INVALID
                        )
                    }
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
        viewModelScope.launch(exceptionHandler) {
            if (address.length < AUTO_SUGGESTION_THRESH_HOLD) return@launch
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
        viewModelScope.launch(exceptionHandler) {
            viewModelState.update { currentState ->
                currentState.copy(addressTextValue = "")
            }
        }
    }

    fun onSuggestedAddressClicked(suggestedAddress: String) {
        viewModelScope.launch(exceptionHandler) {
            val location = locationHelper.locationFromAddress(suggestedAddress)
            if (location.isNullOrInvalid()) return@launch
            viewModelState.update { currentState ->
                currentState.copy(
                    markerPosition = location,
                    addressTextValue = suggestedAddress,
                    dismissAutoSuggestion = true,
                )
            }
        }
    }

    fun onOKButtonClicked() {
        addressNavigator?.navigateToAddEvent(viewModelState.value.addressTextValue ?: "")
    }

    fun onMapLongClicked(latLng: LatLng) {
        viewModelScope.launch {
            val address = locationHelper.addressFromLocation(latLng.toEventLocation())
            if (address.isEmpty()) return@launch
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

    fun onDismissed() {
        viewModelScope.launch(exceptionHandler) {
            viewModelState.update { currentState ->
                currentState.copy(dismissAutoSuggestion = true)
            }
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

