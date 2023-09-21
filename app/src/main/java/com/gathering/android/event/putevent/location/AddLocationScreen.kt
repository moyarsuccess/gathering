package com.gathering.android.event.putevent.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.common.ADDRESS
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.setNavigationResult
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenAddLocationBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ADDRESS
import com.gathering.android.event.model.EventLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AddLocationScreen : FullScreenBottomSheet(), OnMapReadyCallback, AddLocationNavigator {

    private lateinit var binding: ScreenAddLocationBinding

    @Inject
    lateinit var viewModel: AddLocationViewModel

    @Inject
    lateinit var geocoder: Geocoder

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ScreenAddLocationBinding.inflate(LayoutInflater.from(requireContext()))

        val fm = childFragmentManager
        var mapFragment = fm.findFragmentByTag("mapFragment") as? SupportMapFragment
        if (mapFragment == null) {
            mapFragment = SupportMapFragment()
            val ft = fm.beginTransaction()
            ft.add(com.gathering.android.R.id.mapFragmentContainer, mapFragment, "mapFragment")
            ft.commit()
            fm.executePendingTransactions()
        }
        mapFragment.getMapAsync(this)

        if (savedInstanceState != null) {
            lastKnownLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState.getParcelable(KEY_LOCATION, Location::class.java)
            } else {
                savedInstanceState.getParcelable(KEY_LOCATION)
            }
            cameraPosition = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState.getParcelable(KEY_CAMERA_POSITION, CameraPosition::class.java)
            } else {
                savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
            }
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etAddress.initAutoSuggestion()

        binding.btnOk.setOnClickListener {
            viewModel.onOKButtonClicked()
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (state.setSelectedAddress) {
                    binding.etAddress.setText(state.selectedAddress)
                }
                if (state.addMarker) {
                    state.showMarker()
                }
                state.addressList.showAddressAutoSuggestionList()
                if (!state.errorMessage.isNullOrEmpty()) {
                    showErrorText(state.errorMessage)
                }
            }
        }
    }

    private fun AddLocationUiState.showMarker() {
        map?.clear()
        val latLng = selectedLocation.toLatLng()
        Log.d("WTF", "$latLng")
        Log.d("WTF2", "$map")
        moveCamera(latLng)
        map?.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
        )
        hideKeyboard()
    }

    private fun EventLocation.toLatLng(): LatLng {
        return LatLng(
            lat ?: 0.0,
            lon ?: 0.0
        )
    }

    private fun List<String>.showAddressAutoSuggestionList() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.select_dialog_item,
            toTypedArray()
        )
        binding.etAddress.setAdapter(adapter)
    }

    private fun AutoCompleteTextView.initAutoSuggestion() {
        doOnTextChanged { text, _, _, _ ->
            viewModel.onAddressChanged(text.toString())
        }
        onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            viewModel.onSuggestedAddressClicked(text.toString())
        }
    }

    private fun hideKeyboard() {
        val inputSystemService = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
        val imm = inputSystemService as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        this.map?.setOnMapLongClickListener { latLong ->
            viewModel.onMapLongClicked(latLong)
        }
        PermissionX.init(requireActivity()).permissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    adjustMapUi()
                    syncMapWithCurrentDeviceLocation()
                }
            }

        val address = arguments?.getString(ADDRESS)
        viewModel.onViewCreated(address, this)
    }

    @SuppressLint("MissingPermission")
    private fun syncMapWithCurrentDeviceLocation() {
        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener(requireActivity()) { task ->
            if (!task.isSuccessful) return@addOnCompleteListener
            lastKnownLocation = task.result
            if (lastKnownLocation == null) return@addOnCompleteListener
            val latLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            moveCamera(latLng)
        }
    }

    private fun moveCamera(latLng: LatLng) {
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat())
        )
    }

    @SuppressLint("MissingPermission")
    private fun adjustMapUi() {
        if (map == null) return
        map?.isMyLocationEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true
    }

    override fun navigateToAddEvent(address: String) {
        setNavigationResult(KEY_ARGUMENT_SELECTED_ADDRESS, address)
        findNavController().popBackStack()
    }

    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }
}