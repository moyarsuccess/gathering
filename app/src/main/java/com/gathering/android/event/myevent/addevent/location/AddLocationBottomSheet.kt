package com.gathering.android.event.myevent.addevent.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.setNavigationResult
import com.gathering.android.databinding.BottomSheetAddLocationBinding
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddLocationBottomSheet : BottomSheetDialogFragment(), OnMapReadyCallback {

    private lateinit var binding: BottomSheetAddLocationBinding

    @Inject
    lateinit var viewModel: AddLocationViewModel

    @Inject
    lateinit var geocoder: Geocoder

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenCustomBottomSheet)
    }

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddLocationBinding.inflate(LayoutInflater.from(requireContext()))

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

        val fragmentManager = requireActivity().supportFragmentManager

        val mapFragment = fragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.etAddress.initAutoSuggestion()

        binding.btnOk.setOnClickListener {
            val address = binding.etAddress.text.toString()
            viewModel.onOKButtonClicked(address)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                AddLocationViewState.ClearMap -> map?.clear()
                AddLocationViewState.HideKeyboard -> hideKeyboard()
                AddLocationViewState.AdjustMapUi -> adjustMapUi()
                AddLocationViewState.SyncMapWithCurrentDeviceLocation -> syncMapWithCurrentDeviceLocation()
                is AddLocationViewState.ShowError -> showToast(state.errorMessage)
                is AddLocationViewState.ShowAddressList -> showAddressAutoSuggestionList(state)
                is AddLocationViewState.SetAddress -> binding.etAddress.setText(state.address)
                is AddLocationViewState.MoveCamera -> moveCamera(state.latLng)
                is AddLocationViewState.AddMarker -> {
                    map?.addMarker(
                        MarkerOptions().position(state.latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )
                }

                is AddLocationViewState.NavigateToAddEvent -> {
                    setNavigationResult(state.address)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun showAddressAutoSuggestionList(state: AddLocationViewState.ShowAddressList) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.select_dialog_item,
            state.addressList.toTypedArray()
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
                    viewModel.onPermissionGranted()
                }
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        val frg = requireActivity().supportFragmentManager.findFragmentById(R.id.map)
        if (frg != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(frg).commit()
        }
    }

    private fun showToast(text: String?) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }
}