package com.gathering.android.event.putevent.address.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private const val DEFAULT_ZOOM = 15

@Composable
fun GMap(latLng: LatLng, onLongClicked: (latLng: LatLng) -> Unit) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, DEFAULT_ZOOM.toFloat())
    }
    cameraPositionState.position =
        CameraPosition.fromLatLngZoom(latLng, DEFAULT_ZOOM.toFloat())
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLongClick = onLongClicked,
    ) {
        Marker(
            state = MarkerState(
                position = latLng
            ),
        )
    }
}