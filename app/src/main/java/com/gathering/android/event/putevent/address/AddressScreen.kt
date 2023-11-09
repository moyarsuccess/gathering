package com.gathering.android.event.putevent.address

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.KEY_EVENT_LOCATION
import com.gathering.android.common.composables.CustomActionButton
import com.gathering.android.common.setNavigationResult
import com.gathering.android.common.toLatLng
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ADDRESS
import com.gathering.android.event.model.EventLocation
import com.gathering.android.event.putevent.address.composable.AutoSuggestTextField
import com.gathering.android.event.putevent.address.composable.GMap
import com.gathering.android.ui.theme.GatheringTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddressScreen : FullScreenBottomSheet(), AddressNavigator {

    @Inject
    lateinit var viewModel: AddressViewModel

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                GatheringTheme {
                    Surface(
                        modifier = Modifier.wrapContentSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val locationPermission =
                            rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
                        if (locationPermission.status.isGranted) {
                            val state = viewModel.uiState.collectAsState()
                            Address(
                                address = state.value.addressTextValue ?: "",
                                onAddressChanged = viewModel::onAddressChanged,
                                onSuggestedAddressClicked = viewModel::onSuggestedAddressClicked,
                                onOKButtonClicked = viewModel::onOKButtonClicked,
                                expanded = !state.value.dismissAutoSuggestion,
                                onDismissed = viewModel::onDismissed,
                                onClearClicked = viewModel::onClearClicked,
                                onLongClicked = viewModel::onMapLongClicked,
                                addresses = state.value.suggestedAddressList,
                                latLng = state.value.markerPosition.toLatLng()
                            )
                        } else {
                            Text(text = "Access fine location permission required!")
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated(
            eventLocation = extractEventLocation(),
            addressNavigator = this
        )
    }

    private fun extractEventLocation(): EventLocation? {
        val eventLocation: EventLocation? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable(
                    KEY_EVENT_LOCATION,
                    EventLocation::class.java
                )
            } else {
                arguments?.getSerializable(KEY_EVENT_LOCATION) as? EventLocation
            }
        return eventLocation
    }

    override fun navigateToAddEvent(address: String) {
        setNavigationResult(KEY_ARGUMENT_SELECTED_ADDRESS, address)
        findNavController().popBackStack()
    }

    @Composable
    fun Address(
        address: String,
        onAddressChanged: (String) -> Unit,
        onSuggestedAddressClicked: (String) -> Unit,
        onOKButtonClicked: () -> Unit,
        expanded: Boolean,
        onDismissed: () -> Unit,
        onClearClicked: () -> Unit,
        onLongClicked: (latLng: LatLng) -> Unit,
        addresses: List<String>,
        latLng: LatLng,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row {
                Box(
                    modifier = Modifier
                        .weight(7f)
                        .padding(10.dp),
                ) {
                    AutoSuggestTextField(
                        modifier = Modifier,
                        value = address,
                        onValueChanged = onAddressChanged,
                        onItemClicked = onSuggestedAddressClicked,
                        onDismissed = onDismissed,
                        onClearClick = onClearClicked,
                        list = addresses,
                        expanded = expanded,
                        label = "Address",
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(3f)
                        .padding(10.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    CustomActionButton(
                        isLoading = false,
                        text = "OK",
                        onClick = {
                            onOKButtonClicked()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            GMap(latLng, onLongClicked)
        }
    }
}