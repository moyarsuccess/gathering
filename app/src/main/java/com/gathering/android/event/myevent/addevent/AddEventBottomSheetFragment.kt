package com.gathering.android.event.myevent.addevent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.common.setNavigationResult
import com.gathering.android.databinding.BottomSheetAddEventBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ADDRESS
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ATTENDEE_LIST
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_IMAGE
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_MY_EVENT_LIST
import com.gathering.android.event.model.Event
import com.gathering.android.event.model.EventLocation
import com.gathering.android.event.myevent.addevent.invitation.model.Contact
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class AddEventBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetAddEventBinding

    @Inject
    lateinit var geocoder: Geocoder

    @Inject
    lateinit var viewModel: AddEventViewModel

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
        binding = BottomSheetAddEventBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AddEventViewState.AddEventButtonVisibility -> binding.btnAddEvent.isEnabled =
                    state.isAddEventButtonEnabled

                AddEventViewState.NavigateToAddLocation -> {
                    findNavController().navigate(R.id.action_addEventBottomSheetFragment_to_addLocationBottomSheet)
                }

                AddEventViewState.NavigateToAddPic -> {
                    findNavController().navigate(R.id.action_addEventBottomSheetFragment_to_addPicBottomSheet)
                }

                is AddEventViewState.NavigateToMyEvent -> {
                    setNavigationResult(KEY_ARGUMENT_UPDATE_MY_EVENT_LIST, true)
                    findNavController().popBackStack()
                }

                is AddEventViewState.NavigateToInviteFriend -> {
                    val bundle = bundleOf("contact_list" to state.contactList)
                    findNavController().navigate(
                        R.id.action_addEventBottomSheetFragment_to_inviteFriendBottomSheet,
                        bundle
                    )
                }

                is AddEventViewState.ShowError -> showToast(state.errorMessage)
                is AddEventViewState.SetAddress -> binding.tvLocation.text = state.address
                is AddEventViewState.SetAttendeeList -> {
                    attendees = state.attendees
                    val attendeeNum = state.attendees
                        .split(",")
                        .count()
                        .toString()
                    binding.tvAttendees.text = "$attendeeNum $INVITE_PEOPLE"

                }

                AddEventViewState.OpenDatePickerDialog -> openDatePickerDialog()
                AddEventViewState.OpenTimePickerDialog -> openTimePickerDialog()
                is AddEventViewState.SetImage -> {
                    photoUrl = Uri.parse(state.image).toString()
                    binding.imgEvent.setImageURI(Uri.parse(state.image))
                }
            }
        }

        binding.imgEvent.setOnClickListener {
            viewModel.onImageButtonClicked()
        }

        binding.btnDate.setOnClickListener {
            viewModel.onDateButtonClicked()
        }

        binding.btnTime.setOnClickListener {
            viewModel.onTimeButtonClicked()
        }

        binding.btnLocation.setOnClickListener {
            viewModel.onLocationButtonClicked()
        }

        binding.btnInvitation.setOnClickListener {
            viewModel.onInvitationButtonClicked()
        }

        binding.btnAddEvent.setOnClickListener {
            viewModel.onAddEventButtonClicked(makeCurrentEvent())
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

        getNavigationResultLiveData<List<Contact>>(KEY_ARGUMENT_SELECTED_ATTENDEE_LIST)?.observe(
            viewLifecycleOwner
        ) { attendeeList ->
            viewModel.onAttendeeListChanged(attendeeList)
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun makeCurrentEvent(): Event {
        return Event(
            eventName = binding.etEventName.text.toString(),
            hostName = binding.etHost.text.toString(),
            description = binding.etDescription.text.toString(),
            photoUrl = photoUrl,
            location = locationFromAddressLine(binding.tvLocation.text.toString()),
            dateAndTime = currentTimestamp(),
            isContactEvent = false,
            isMyEvent = true,
            attendees = attendees.split(",")
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
                binding.tvTime.text = selectedTime
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
                binding.tvDate.text = selectedDate
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

    companion object {
        const val INVITE_PEOPLE = "people invited to this event"
    }
}