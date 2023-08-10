package com.gathering.android.event.putevent.location

import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gathering.android.common.ActiveMutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddLocationViewModel @Inject constructor(
    private val placesClient: PlacesClient,
    private val geocoder: Geocoder
) : ViewModel() {

    private val _viewState = ActiveMutableLiveData<AddLocationViewState>()
    val viewState: ActiveMutableLiveData<AddLocationViewState> by ::_viewState

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
                _viewState.setValue(AddLocationViewState.ShowAddressList(addresses))
            }
            .addOnFailureListener {
                _viewState.setValue(AddLocationViewState.ShowError(it.message))
            }
    }

    fun onSuggestedAddressClicked(suggestedAddress: String) {
        val latLng = getLatLong(suggestedAddress) ?: return
        _viewState.setValue(AddLocationViewState.ClearMap)
        _viewState.setValue(AddLocationViewState.AddMarker(latLng))
        _viewState.setValue(AddLocationViewState.MoveCamera(latLng))
        _viewState.setValue(AddLocationViewState.HideKeyboard)
    }

    private fun getLatLong(address: String): LatLng? {
        val addresses: List<Address>? = geocoder.getFromLocationName(address, 1)
        val lat = addresses?.getOrNull(0)?.latitude
        val long = addresses?.getOrNull(0)?.longitude
        if (lat == null || long == null) return null
        return LatLng(lat.toDouble(), long.toDouble())
    }

    fun onOKButtonClicked(address: String) {
        _viewState.setValue(AddLocationViewState.NavigateToAddEvent(address))
    }

    fun onMapLongClicked(latLong: LatLng) {
        getAddress(latLong) { address ->
            _viewState.setValue(AddLocationViewState.SetAddress(address))
            _viewState.setValue(AddLocationViewState.ClearMap)
            _viewState.setValue(AddLocationViewState.AddMarker(latLong))
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
                    val address = addresses.getOrNull(0)?.getAddressLine(0).toString()
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

    fun onPermissionGranted() {
        _viewState.setValue(AddLocationViewState.AdjustMapUi)
        _viewState.setValue(AddLocationViewState.SyncMapWithCurrentDeviceLocation)
    }

    companion object {
        private const val AUTO_SUGGESTION_THRESH_HOLD = 3
    }
}