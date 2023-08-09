package com.gathering.android.event.putevent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.ATTENDEE_LIST
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.common.setNavigationResult
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenPutEventBinding
import com.gathering.android.event.Event
import com.gathering.android.event.KEY_ARGUMENT_EVENT
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ADDRESS
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ATTENDEE_LIST
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_IMAGE
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_MY_EVENT_LIST
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PutEventScreen : FullScreenBottomSheet(), PutEventNavigator {

    lateinit var binding: ScreenPutEventBinding

    @Inject
    lateinit var viewModel: PutEventViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

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
                binding.etDate.text = uiState.date
                binding.etTime.text = uiState.time
                binding.etAttendees.text = uiState.attendees
                binding.etEventName.setNonIdenticalText(uiState.eventName)
                binding.etDescription.setNonIdenticalText(uiState.eventDescription)
                binding.tvLocation.text = uiState.address
                binding.btnAddEvent.text = uiState.btnText
                binding.btnAddEvent.isEnabled = uiState.enableActionButton
                if (uiState.showProgress) {
                    binding.btnAddEvent.startAnimation()
                } else {
                    binding.btnAddEvent.revertAnimation()
                }
                if (!uiState.errorMessage.isNullOrEmpty()) {
                    showErrorText(uiState.errorMessage)
                }
                if (uiState.phoneImageUri != null) {
                    binding.imgEvent.setImageURI(Uri.parse(uiState.phoneImageUri))
                    return@collectLatest
                }
                if (uiState.networkImageUri != null) {
                    imageLoader.loadImage(uiState.networkImageUri, binding.imgEvent)
                }
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
            viewModel.onAddressButtonClicked()
        }

        binding.etAttendees.setOnClickListener {
            viewModel.onAttendeeButtonClicked()
        }

        binding.btnAddEvent.setOnClickListener {
            viewModel.onActionButtonClicked()
        }

        binding.etEventName.doOnTextChanged { text, _, _, _ ->
            viewModel.onEventNameChanged(text.toString())
        }

        binding.etDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.onDescriptionChanged(text.toString())
        }

        getNavigationResultLiveData<String>(KEY_ARGUMENT_SELECTED_ADDRESS)?.observe(
            viewLifecycleOwner
        ) { address ->
            viewModel.onAddressSelected(address)
        }

        getNavigationResultLiveData<String>(KEY_ARGUMENT_SELECTED_IMAGE)?.observe(
            viewLifecycleOwner
        ) { image ->
            viewModel.onImageSelected(image)
        }

        getNavigationResultLiveData<List<String>>(KEY_ARGUMENT_SELECTED_ATTENDEE_LIST)?.observe(
            viewLifecycleOwner
        ) { attendeeList ->
            viewModel.onAttendeeListSelected(attendeeList)
        }

        val event = arguments?.getSerializable(KEY_ARGUMENT_EVENT) as? Event
        viewModel.onViewCreated(putEventNavigator = this, event = event)
    }

    private fun EditText.setNonIdenticalText(text:String) {
        if (this.text.toString() == text) return
        setText(text)
    }

    override fun navigateToAddressSelector() {
        findNavController().navigate(R.id.action_addEventBottomSheetFragment_to_addLocationBottomSheet)
    }

    override fun navigateToDateSelector(latestYear: Int, latestMonth: Int, latestDay: Int) {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                viewModel.onNewDateSelected(selectedYear, selectedMonth, selectedDay)
            },
            latestYear,
            latestMonth,
            latestDay
        )
        datePickerDialog.show()
    }

    override fun navigateToTimeSelector(latestHour: Int, latestMinute: Int) {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                viewModel.onNewTimeSelected(selectedHour, selectedMinute)
            },
            latestHour,
            latestMinute,
            true
        )

        timePickerDialog.show()
    }

    override fun navigateImageSelector() {
        findNavController().navigate(R.id.action_addEventBottomSheetFragment_to_addPicBottomSheet)
    }

    override fun dismissPutEvent() {
        setNavigationResult(KEY_ARGUMENT_UPDATE_MY_EVENT_LIST, true)
        findNavController().popBackStack()
    }

    override fun navigateToAttendeesSelector(attendees: List<String>) {
        val bundle = bundleOf(ATTENDEE_LIST to attendees)
        findNavController().navigate(
            R.id.action_addEventBottomSheetFragment_to_inviteFriendBottomSheet,
            bundle
        )
    }
}