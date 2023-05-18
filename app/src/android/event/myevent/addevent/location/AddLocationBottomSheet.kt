package com.gathering.android.event.myevent.addevent.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.BuildConfig
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.permissionx.guolindev.PermissionX
import java.util.Locale

class AddLocationBottomSheet : BottomSheetDialogFragment(), OnMapReadyCallback {

    private lateinit var binding: BottomSheetAddLocationBinding

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var lastKnownLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenCustomBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        if (savedInstanceState != null) {
            // getParsable with string and generic class
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
        binding = BottomSheetAddLocationBinding.inflate(LayoutInflater.from(requireContext()))

        Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(requireContext())

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
            setNavigationResult(address)
            findNavController().popBackStack()
        }
    }

    private fun AutoCompleteTextView.initAutoSuggestion() {
        doOnTextChanged { text, _, _, _ ->
            if (text.toString().length < AUTO_SUGGESTION_THRESH_HOLD) return@doOnTextChanged
            val token = AutocompleteSessionToken.newInstance()
            val request = FindAutocompletePredictionsRequest
                .builder()
                .setSessionToken(token)
                .setCountries(listOf("CA", "US"))
                .setQuery(text.toString())
                .build()
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val addresses = response
                        .autocompletePredictions
                        .map {
                            it.getPrimaryText(null).toString()
                        }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.select_dialog_item,
                        addresses.toTypedArray()
                    )
                    setAdapter(adapter)

                }
                .addOnFailureListener {
                    Log.i("WTF-4", "Error $it")
                }
        }

        onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            showLocationOnMap()
            hideKeyboard()
        }
    }


    private fun View.hideKeyboard() {
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
        this.map?.setOnMapLongClickListener {
            val address = getAddress(it.latitude, it.longitude)
            binding.etAddress.setText(address)
            map.clear()
            map.addMarker(
                MarkerOptions().position(it).title("You are here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        }

        this.map?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                // Inflate the layouts for the info window, title and snippet.
                val infoWindow = layoutInflater.inflate(
                    R.layout.custom_info_contents, binding.root, false
                )
                val title = infoWindow.findViewById<TextView>(com.gathering.android.R.id.title)
                title.text = marker.title
                val snippet = infoWindow.findViewById<TextView>(com.gathering.android.R.id.snippet)
                snippet.text = marker.snippet
                return infoWindow
            }
        })
        PermissionX.init(requireActivity()).permissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    updateLocationUI()
                    getDeviceLocation()
                }
            }
    }

    private fun showLocationOnMap() {
        val latLOng = getLatLong(binding.etAddress.text.toString())
        map?.clear()
        map?.addMarker(
            MarkerOptions().position(latLOng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLOng, DEFAULT_ZOOM.toFloat()))
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return addresses?.get(0)?.getAddressLine(0).toString()
    }

    private fun getLatLong(address: String): LatLng {
        val geocoder = Geocoder(requireContext())
        val addresses: List<Address>? = geocoder.getFromLocationName(address, 1)
        val lat = addresses?.get(0)?.latitude
        val long = addresses?.get(0)?.longitude
        return LatLng(lat!!.toDouble(), long!!.toDouble())
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener(requireActivity()) { task ->
            if (!task.isSuccessful) return@addOnCompleteListener
            lastKnownLocation = task.result
            if (lastKnownLocation == null) return@addOnCompleteListener
            map?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        lastKnownLocation!!.latitude, lastKnownLocation!!.longitude
                    ), DEFAULT_ZOOM.toFloat()
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val frg = requireActivity().supportFragmentManager.findFragmentById(R.id.map)
        if (frg != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(frg).commit()
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) return
        map?.isMyLocationEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true
    }

    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val AUTO_SUGGESTION_THRESH_HOLD = 3
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }
}