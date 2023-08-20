package com.gathering.android.event.putevent.location

import com.google.android.gms.maps.model.LatLng

sealed interface AddLocationViewState {
    class NavigateToAddEvent(val address: String) : AddLocationViewState

    class ShowError(val errorMessage: String?) : AddLocationViewState

    class ShowAddressList(val addressList: List<String>) : AddLocationViewState

    class SetAddress(val address: String) : AddLocationViewState

    object ClearMap : AddLocationViewState

    class AddMarker(val latLng: LatLng) : AddLocationViewState

    class MoveCamera(val latLng: LatLng) : AddLocationViewState

    object HideKeyboard : AddLocationViewState

    object AdjustMapUi : AddLocationViewState

    object SyncMapWithCurrentDeviceLocation : AddLocationViewState
}