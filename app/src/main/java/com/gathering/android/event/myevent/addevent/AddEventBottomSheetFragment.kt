package com.gathering.android.event.myevent.addevent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.location.Location
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
import com.gathering.android.databinding.BottomSheetAddEventBinding
import com.gathering.android.event.home.model.Event
import com.gathering.android.event.myevent.addevent.invitation.model.Contact
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date
import javax.inject.Inject


@AndroidEntryPoint
class AddEventBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetAddEventBinding

    @Inject
    lateinit var viewModel: AddEventViewModel

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
                    val attendeeNum = state.attendees
                        .split(",")
                        .count()
                        .toString()
                    binding.tvAttendees.text = "$attendeeNum people invited to this event "

                }

                AddEventViewState.OpenDatePickerDialog -> openDatePickerDialog()
                AddEventViewState.OpenTimePickerDialog -> openTimePickerDialog()
                is AddEventViewState.SetImage -> binding.imgEvent.setImageURI(Uri.parse(state.image))
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
            viewModel.onAddEventButtonClicked(provideEvent())
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

    private fun provideEvent(): Event {
        return Event(
            eventId = "1",
            eventName = binding.etEventName.text.toString(),
            hostName = binding.etHost.text.toString(),
            description = binding.etDescription.toString(),
            photoUrl = "",
            location = Location("").also { it.altitude = 45.5019; it.longitude = 73.5674 },
            time = binding.tvTime.text.toString(),
            date = Date(),
            isContactEvent = false,
            isMyEvent = true,
            activities = listOf("party", "dinner", "drink", "cake", "tea")
        )
    }

    private fun openTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                binding.tvTime.text = selectedTime
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun openDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.tvDate.text = selectedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}