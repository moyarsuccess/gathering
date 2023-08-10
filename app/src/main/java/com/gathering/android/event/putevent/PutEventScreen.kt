package com.gathering.android.event.putevent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.gathering.android.common.*
import com.gathering.android.databinding.ScreenPutEventBinding
import com.gathering.android.event.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PutEventScreen : FullScreenBottomSheet(), PutEventNavigator {

    lateinit var binding: ScreenPutEventBinding

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var viewModel: PutEventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ScreenPutEventBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.etEventName.setNonIdenticalText(state.eventName ?: "")
                binding.etDescription.setNonIdenticalText(state.eventDescription ?: "")
                binding.btnAction.text = state.actionButtonText
                binding.btnAction.isEnabled = state.actionButtonEnable ?: false
                println("WTF - 2 ${state.actionButtonEnable ?: false}")
                binding.tvDate.text = state.eventDate
                binding.tvTime.text = state.eventTime
                binding.tvLocation.text = state.eventAddress
                imageLoader.loadImage(state.imageUri, binding.imgEvent)
                binding.tvAttendees.text = state.eventAttendees
                if (!state.errorMessage.isNullOrEmpty()) {
                    showErrorText(state.errorMessage)
                }
                if (state.showProgress == true) {
                    binding.btnAction.startAnimation()
                } else {
                    binding.btnAction.revertAnimation()
                }
            }
        }

        binding.imgEvent.setOnClickListener {
            viewModel.onImageButtonClicked()
        }

        binding.tvDate.setOnClickListener {
            viewModel.onDateButtonClicked()
        }

        binding.tvTime.setOnClickListener {
            viewModel.onTimeButtonClicked()
        }

        binding.tvLocation.setOnClickListener {
            viewModel.onLocationButtonClicked()
        }

        binding.tvAttendees.setOnClickListener {
            viewModel.onAttendeeButtonClicked()
        }

        binding.btnAction.setOnClickListener {
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
            viewModel.onNewLocationSelected(address)
        }

        getNavigationResultLiveData<String>(KEY_ARGUMENT_SELECTED_IMAGE)?.observe(
            viewLifecycleOwner
        ) { image ->
            viewModel.onImageSelected(image)
        }

        getNavigationResultLiveData<List<String>>(KEY_ARGUMENT_SELECTED_ATTENDEE_LIST)?.observe(
            viewLifecycleOwner
        ) { attendeeList ->
            viewModel.onNewAttendeeListSelected(attendeeList)
        }

        val event = arguments?.getSerializable(KEY_ARGUMENT_EVENT) as? Event
        viewModel.onViewCreated(event, this)
    }


    override fun navigateToImagePicker() {
        findNavController().navigate(R.id.action_putEventBottomSheetFragment_to_addPicBottomSheet)
    }

    override fun navigateToDatePicker(year: Int, month: Int, day: Int) {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                viewModel.onNewDateSelected(selectedYear, selectedMonth, selectedDay)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    override fun navigateToTimePicker(hour: Int, minute: Int) {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                viewModel.onNewTimeSelected(selectedHour, selectedMinute)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    override fun navigateToLocationPicker() {
        findNavController().navigate(R.id.action_putEventBottomSheetFragment_to_addLocationBottomSheet)
    }

    override fun navigateToAttendeesPicker(attendees: String) {
        val bundle = bundleOf(ATTENDEE_LIST to attendees)
        findNavController().navigate(
            R.id.action_putEventBottomSheetFragment_to_inviteFriendBottomSheet,
            bundle
        )
    }

    override fun dismissPutEvent() {
        setNavigationResult(KEY_ARGUMENT_UPDATE_MY_EVENT_LIST, true)
        findNavController().popBackStack()
    }

    private fun EditText.setNonIdenticalText(text: String) {
        if (this.text.toString() == text) return
        setText(text)
    }

}