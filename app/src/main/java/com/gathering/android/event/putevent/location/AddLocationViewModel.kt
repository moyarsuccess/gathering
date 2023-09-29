package com.gathering.android.event.putevent.location

import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.event.model.EventLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddLocationViewModel @Inject constructor(
    private val placesClient: PlacesClient,
    private val geocoder: Geocoder
) : ViewModel() {

    private var addLocationNavigator: AddLocationNavigator? = null

    private val viewModelState = MutableStateFlow(AddLocationViewModelState())
    val uiState: Flow<AddLocationUiState> = viewModelState.map { viewModelState ->
        AddLocationUiState(
            addressList = viewModelState.addressList,
            selectedAddress = viewModelState.selectedAddress,
            selectedLocation = viewModelState.selectedLocation,
            errorMessage = viewModelState.errorMessage,
            okButtonEnable = viewModelState.okButtonEnable,
            addMarker = viewModelState.addMarker,
            setSelectedAddress = viewModelState.setSelectedAddress,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AddLocationUiState()
    )

    fun onViewCreated(address: String?, addLocationNavigator: AddLocationNavigator) {
        this.addLocationNavigator = addLocationNavigator
        if (address.isNullOrEmpty()) return
        viewModelState.update { currentState ->
            currentState.copy(
                selectedAddress = address,
                selectedLocation = address.locationFromAddressLine(),
                okButtonEnable = false,
                addMarker = true,
            )
        }
    }

    fun onAddressChanged(address: String) {
        if (address.length < AUTO_SUGGESTION_THRESH_HOLD) return
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest
            .builder()
            .setSessionToken(token)
            .setCountries(listOf("CA", "US"))
            .setQuery(address)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val addresses = response.autocompletePredictions.map {
                    it.getPrimaryText(null).toString()
                }
                viewModelState.update { currentState ->
                    currentState.copy(
                        addressList = addresses,
                        okButtonEnable = true,
                        addMarker = false,
                        setSelectedAddress = false,
                    )
                }
            }
            .addOnFailureListener {
                viewModelState.update { currentState ->
                    currentState.copy(
                        errorMessage = it.message,
                        addMarker = false,
                        setSelectedAddress = false,
                    )
                }
            }
    }

    fun onSuggestedAddressClicked(suggestedAddress: String) {
        val latLng = getLatLong(suggestedAddress)
        println("WTF - $latLng")
        if (latLng == null) return
        viewModelState.update { currentState ->
            currentState.copy(
                selectedLocation = EventLocation(latLng.latitude, latLng.longitude),
                selectedAddress = suggestedAddress,
                addMarker = true,
                setSelectedAddress = true,
            )
        }
    }

    private fun getLatLong(address: String): LatLng? {
        val addresses: List<Address>? = geocoder.getFromLocationName(address, 1)
        val lat = addresses?.getOrNull(0)?.latitude
        val long = addresses?.getOrNull(0)?.longitude
        if (lat == null || long == null) return null
        return LatLng(lat.toDouble(), long.toDouble())
    }


    fun onOKButtonClicked() {
        addLocationNavigator?.navigateToAddEvent(viewModelState.value.selectedAddress ?: "")
    }

    fun onMapLongClicked(latLong: LatLng) {
        getAddress(latLong) { address ->
            viewModelState.update { currentState ->
                currentState.copy(
                    selectedAddress = address,
                    okButtonEnable = true,
                    selectedLocation = EventLocation(
                        lat = latLong.latitude,
                        lon = latLong.longitude,
                    ),
                    addMarker = true,
                    setSelectedAddress = true,
                )
            }
        }
    }

    private fun getAddress(
        latLng: LatLng,
        onAddressReady: (String) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            ) { addresses ->
                viewModelScope.launch {
                    val address = addresses
                        .getOrNull(0)
                        ?.getAddressLine(0)
                        .toString()
                    onAddressReady(address)
                }
            }
        } else {
            viewModelScope.launch {
                val address = getAddressFromLocation(latLng)
                onAddressReady(address)
            }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun getAddressFromLocation(latLng: LatLng): String =
        withContext(Dispatchers.IO) {
            return@withContext geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )?.getOrNull(0)?.getAddressLine(0).toString()
        }

    private fun String.locationFromAddressLine(): EventLocation {
        val addressList = geocoder
            .getFromLocationName(this, 1)
            ?: return EventLocation()

        val lat = addressList.firstOrNull()?.latitude ?: 0.0
        val long = addressList.firstOrNull()?.longitude ?: 0.0
        return EventLocation(lat, long)
    }

    private data class AddLocationViewModelState(
        val addressList: List<String> = listOf(),
        val selectedAddress: String? = null,
        val selectedLocation: EventLocation = EventLocation(0.0, 0.0),
        val errorMessage: String? = null,
        val okButtonEnable: Boolean? = false,
        val addMarker: Boolean = true,
        val setSelectedAddress: Boolean = true,
    )

    companion object {
        private const val AUTO_SUGGESTION_THRESH_HOLD = 3
    }
}

