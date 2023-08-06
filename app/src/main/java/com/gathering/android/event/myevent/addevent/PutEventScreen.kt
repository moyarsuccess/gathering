package com.gathering.android.event.myevent.addevent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.ATTENDEE_LIST
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.SEPARATOR
import com.gathering.android.common.SOMETHING_WRONG
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.common.setNavigationResult
import com.gathering.android.databinding.ScreenPutEventBinding
import com.gathering.android.event.Event
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ADDRESS
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ATTENDEE_LIST
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_IMAGE
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_MY_EVENT_LIST
import com.gathering.android.event.model.EventLocation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class PutEventScreen : BottomSheetDialogFragment() {

    lateinit var binding: ScreenPutEventBinding

    @Inject
    lateinit var geocoder: Geocoder

    @Inject
    lateinit var viewModel: PutEventViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    private var attendees: String = ""

    private var photoUrl: String = ""

    private var calender: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenCustomBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ScreenPutEventBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                binding.etDate.setText(uiState.date)
                binding.etTime.setText(uiState.time)
                binding.etAttendees.setText(uiState.attendees)
                binding.etDescription.setText(uiState.eventDescription)
                binding.etEventName.setText(uiState.eventName)
                binding.tvLocation.text = uiState.address
                binding.btnAddEvent.text = uiState.btnText
                if (uiState.phoneImageUri != null) {
                    photoUrl = Uri.parse(uiState.phoneImageUri).toString()
                    binding.imgEvent.setImageURI(Uri.parse(uiState.phoneImageUri))
                    return@collectLatest
                }
                if (uiState.networkImageUri != null) {
                    imageLoader.loadImage(uiState.networkImageUri, binding.imgEvent)
                    return@collectLatest
                }
            }
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PutEventViewState.PutEventButtonVisibility -> binding.btnAddEvent.isEnabled =
                    state.isAddEventButtonEnabled

                PutEventViewState.NavigateToPutLocation -> {
                    findNavController().navigate(R.id.action_addEventBottomSheetFragment_to_addLocationBottomSheet)
                }

                PutEventViewState.NavigateToPutPic -> {
                    findNavController().navigate(R.id.action_addEventBottomSheetFragment_to_addPicBottomSheet)
                }

                is PutEventViewState.NavigateToMyEvent -> {
                    setNavigationResult(KEY_ARGUMENT_UPDATE_MY_EVENT_LIST, true)
                    findNavController().popBackStack()
                }

                is PutEventViewState.NavigateToInviteFriend -> {
                    val attendees = state.attendeeList.joinToString(SEPARATOR) { it }
                    val bundle = bundleOf(ATTENDEE_LIST to attendees)
                    findNavController().navigate(
                        R.id.action_addEventBottomSheetFragment_to_inviteFriendBottomSheet,
                        bundle
                    )
                }

                is PutEventViewState.ShowError -> Log.d(
                    SOMETHING_WRONG,
                    state.errorMessage.toString()
                )

                is PutEventViewState.SetAddress -> binding.tvLocation.text = state.address
                is PutEventViewState.SetAttendeeList -> {
                    attendees = state.attendees
                    val attendeeNum = state.attendees
                        .split(",")
                        .count()
                        .toString()
                    binding.etAttendees.setText(
                        getString(
                            R.string.attendees_count_hint_text,
                            attendeeNum
                        )
                    )

                }

                PutEventViewState.OpenDatePickerDialog -> openDatePickerDialog()
                PutEventViewState.OpenTimePickerDialog -> openTimePickerDialog()
                is PutEventViewState.SetImage -> {
                    photoUrl = Uri.parse(state.image).toString()
                    binding.imgEvent.setImageURI(Uri.parse(state.image))
                }

                PutEventViewState.MorphPutEventButtonToProgress -> binding.btnAddEvent.startAnimation()
                PutEventViewState.RevertPutEventProgressToButton -> binding.btnAddEvent.revertAnimation()
            }
        }

        binding.imgEvent.setOnClickListener {
            viewModel.onImageButtonClicked()
        }

        binding.etDate.setOnClickListener {
            viewModel.onDateButtonClicked()
        }

        binding.etTime.setOnClickListener {
            viewModel.onTimeButtonClicked()
        }

        binding.tvLocation.setOnClickListener {
            viewModel.onLocationButtonClicked()
        }

        binding.etAttendees.setOnClickListener {
            viewModel.onInvitationButtonClicked()
        }

        binding.btnAddEvent.setOnClickListener {

            viewModel.onAddEventButtonClicked(makeCurrentEvent())
        }

        binding.etEventName.doOnTextChanged { text, _, _, _ ->
            viewModel.onEventNameChanged(text.toString())
        }

        binding.etDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.onDescriptionChanged(text.toString())
        }

        binding.etDate.doOnTextChanged { text, _, _, _ ->
            viewModel.onNewDateSelected(text.toString())
        }

        binding.etTime.doOnTextChanged { text, _, _, _ ->
            viewModel.onTimeChanged(text.toString())
        }

        binding.tvLocation.doOnTextChanged { text, _, _, _ ->
            viewModel.onEventNameChanged(text.toString())
        }


        getNavigationResultLiveData<String>(KEY_ARGUMENT_SELECTED_ADDRESS)?.observe(
            viewLifecycleOwner
        ) { address ->
            viewModel.onAddressChanged(address)
        }

        getNavigationResultLiveData<String>(KEY_ARGUMENT_SELECTED_IMAGE)?.observe(
            viewLifecycleOwner
        ) { image ->
            viewModel.onImageSelected(image)
        }

        getNavigationResultLiveData<List<String>>(KEY_ARGUMENT_SELECTED_ATTENDEE_LIST)?.observe(
            viewLifecycleOwner
        ) { attendeeList ->
            viewModel.onAttendeeListChanged(attendeeList)
        }

        viewModel.onViewCreated()
    }

    private fun makeCurrentEvent(): Event {
        return Event(
            eventId = 0,
            eventName = binding.etEventName.text.toString(),
            eventHostEmail = "",
            description = binding.etDescription.text.toString(),
            photoUrl = photoUrl,
            location = locationFromAddressLine(binding.tvLocation.text.toString()),
            dateAndTime = currentTimestamp(),
            isContactEvent = false,
            isMyEvent = true,
            attendees = attendees.split(","),
            liked = false
        )
    }

    private fun currentTimestamp(): Long {
        return calender.time.time
    }

    private fun locationFromAddressLine(address: String): EventLocation {
        val addressList = geocoder
            .getFromLocationName(address, 1)
            ?: return EventLocation()

        val lat = addressList.firstOrNull()?.latitude ?: 0.0
        val long = addressList.firstOrNull()?.longitude ?: 0.0
        val addressLine = addressList.firstOrNull()?.getAddressLine(0) ?: ""
        return EventLocation(lat, long, addressLine)
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