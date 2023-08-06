package com.gathering.android.event.myevent.editMyEvent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.BottomSheetEditMyEventBinding
import com.gathering.android.event.Event
import com.gathering.android.event.KEY_ARGUMENT_EVENT
import com.gathering.android.event.model.EventLocation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class EditMyEventBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetEditMyEventBinding

    @Inject
    lateinit var viewModel: EditMyEventViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    private var attendees: String = ""

    private var photoUrl: String = ""

    lateinit var location: EventLocation

    private var calender: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenCustomBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetEditMyEventBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val event = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(KEY_ARGUMENT_EVENT, Event::class.java)
        } else {
            arguments?.getSerializable(KEY_ARGUMENT_EVENT) as Event
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EditMyEventViewState.NavigateToMyEvent -> {
                    findNavController().popBackStack()
                }

                is EditMyEventViewState.SetAttendees -> {
                    attendees = state.attendees.joinToString(",")
                    binding.etAttendees.setText(attendees)
                }

                is EditMyEventViewState.SetDescription -> {
                    binding.etDescription.setText(state.description)
                }

                is EditMyEventViewState.SetEventName -> {
                    binding.etEventName.setText(state.eventName)
                }

                is EditMyEventViewState.SetTime -> {
                    binding.etTime.setText(state.time)
                }

                is EditMyEventViewState.SetDate -> {
                    binding.etDate.setText(state.date)
                }

                is EditMyEventViewState.SetLocation -> {
                    location = state.Location
                    binding.tvLocation.text =
                        getAddressFromLatLng(
                            latitude = location.lat ?: 0.0,
                            longitude = location.lon ?: 0.0
                        )
                }

                is EditMyEventViewState.ShowError -> showErrorText(state.errorMessage)
                is EditMyEventViewState.SetPhoto -> {
                    imageLoader.loadImage(state.photo, binding.imgEvent)
                }
            }
        }

        binding.btnAddEvent.setOnClickListener {
            viewModel.onEditButtonClicked(makeCurrentEvent())
        }

        event?.also {
            viewModel.onViewCreated(it)
        }
    }

    private fun makeCurrentEvent(): Event {
        return Event(
            eventId = 0,
            eventName = binding.etEventName.text.toString(),
            eventHostEmail = "",
            description = binding.etDescription.text.toString(),
            photoUrl = photoUrl,
            location = location,
            dateAndTime = calender.time.time,
            isContactEvent = false,
            isMyEvent = true,
            attendees = attendees.split(","),
            liked = false
        )
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var addressText = "Address not found"

        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) as List<Address>

            addressText = if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]

                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append(", ")
                }
                sb.toString().trim()
            } else {
                "No address found for the given coordinates."
            }
        } catch (e: IOException) {
            e.printStackTrace()
            addressText = "Geocoder error: ${e.message}"
        }
        return addressText
    }

    private fun openTimePickerDialog() {
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minute = calender.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                binding.etTime.setText(selectedTime)
                calender.set(Calendar.HOUR_OF_DAY, selectedHour)
                calender.set(Calendar.MINUTE, selectedMinute)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun openDatePickerDialog() {
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.etDate.setText(selectedDate)
                calender.set(Calendar.YEAR, selectedYear)
                calender.set(Calendar.MONTH, selectedMonth)
                calender.set(Calendar.DAY_OF_MONTH, selectedDay)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

}