package com.gathering.android.event.putevent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.gathering.android.R
import com.gathering.android.common.ATTENDEE_LIST
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.KEY_EVENT_LOCATION
import com.gathering.android.common.composables.CustomActionButton
import com.gathering.android.common.composables.CustomTextField
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.setNavigationResult
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenPutEventBinding
import com.gathering.android.event.Event
import com.gathering.android.event.KEY_ARGUMENT_EVENT
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ADDRESS
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ATTENDEE_LIST
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_IMAGE
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_MY_EVENT_LIST
import com.gathering.android.event.model.EventLocation
import com.gathering.android.ui.theme.GatheringTheme
import com.gathering.android.ui.theme.customBackgroundColor
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
        return if (!isComposeEnabled) {
            binding = ScreenPutEventBinding.inflate(LayoutInflater.from(requireContext()))
            return binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            modifier = Modifier.wrapContentSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val state = viewModel.uiState.collectAsState()
                            PutEvent(
                                photoUrl = state.value.imageUri,
                                eventName = state.value.eventName,
                                eventDate = state.value.eventDate ?: "",
                                eventTime = state.value.eventTime,
                                eventAddress = state.value.eventAddress,
                                eventAttendee = state.value.eventAttendees,
                                eventDescription = state.value.eventDescription,
                                isInProgress = state.value.showProgress,
                                onEventNameChanged = viewModel::onEventNameChanged,
                                onDescriptionChanged = viewModel::onDescriptionChanged,
                                onImageButtonClicked = viewModel::onImageButtonClicked,
                                onEventDateClicked = viewModel::onDateButtonClicked,
                                onEventTimeClicked = viewModel::onTimeButtonClicked,
                                onEventLocationClicked = viewModel::onLocationButtonClicked,
                                onEventAttendeeClicked = viewModel::onAttendeeButtonClicked,
                                onEventActionButtonClicked = viewModel::onActionButtonClicked
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val event = arguments?.getSerializable(KEY_ARGUMENT_EVENT) as? Event

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
        if (isComposeEnabled) {
            viewModel.onViewCreated(event, this)
            return
        }
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.etEventName.setNonIdenticalText(state.eventName ?: "")
                binding.etDescription.setNonIdenticalText(state.eventDescription ?: "")
                binding.btnAction.text = state.actionButtonText
                binding.btnAction.isEnabled = state.actionButtonEnable ?: false
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

        viewModel.onViewCreated(event, this)
    }


    override fun navigateToImagePicker() {
        findNavController().navigate(R.id.action_putEventBottomSheetFragment_to_addPicScreen)
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

    override fun navigateToLocationPicker(eventLocation: EventLocation) {
        val bundle = bundleOf(KEY_EVENT_LOCATION to eventLocation)
        findNavController().navigate(
            R.id.action_putEventScreen_to_addLocationScreen,
            bundle
        )
    }

    override fun navigateToAttendeesPicker(attendees: String) {
        val bundle = bundleOf(ATTENDEE_LIST to attendees)
        findNavController().navigate(
            R.id.action_putEventScreen_to_addAttendees,
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

    @Composable
    fun PutEvent(
        photoUrl: String?,
        eventName: String?,
        eventDate: String?,
        eventTime: String?,
        eventAddress: String?,
        eventAttendee: String?,
        eventDescription: String?,
        isInProgress: Boolean?,
        onImageButtonClicked: () -> Unit,
        onEventNameChanged: (String) -> Unit,
        onDescriptionChanged: (String) -> Unit,
        onEventDateClicked: () -> Unit,
        onEventTimeClicked: () -> Unit,
        onEventLocationClicked: () -> Unit,
        onEventAttendeeClicked: () -> Unit,
        onEventActionButtonClicked: () -> Unit,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            EventImage(photoUrl ?: "", 200.dp, Modifier.clickable {
                onImageButtonClicked()
            })
            CustomTextField(
                value = eventName ?: "",
                onValueChange = {
                    onEventNameChanged(it)
                },
                label = "Event Name",
                modifier = Modifier.fillMaxWidth(),
            )
            CustomTextField(
                value = eventDate ?: "",
                onClicked = { onEventDateClicked() },
                label = "Event Date",
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
            )

            CustomTextField(
                value = eventTime ?: "",
                onClicked = { onEventTimeClicked() },
                label = "Event Time",
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
            )

            CustomTextField(
                value = eventAddress ?: "",
                onClicked = {
                    onEventLocationClicked()
                },
                label = "Event Address",
                readOnly = true,
                maxLine = 2,
                modifier = Modifier.fillMaxWidth(),
            )

            CustomTextField(
                value = eventAttendee ?: "",
                onValueChange = {},
                onClicked = { onEventAttendeeClicked() },
                label = "Event Attendee",
                readOnly = true,
                maxLine = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            CustomTextField(
                value = eventDescription ?: "",
                onValueChange = {
                    onDescriptionChanged(it)
                },
                maxLine = 3,
                label = "Event Detail",
                modifier = Modifier.fillMaxWidth(),
            )

            CustomActionButton(
                isLoading = isInProgress,
                text = "Save Changes",
                onClick = { onEventActionButtonClicked() },
                modifier = Modifier
                    .height(60.dp)
                    .width(170.dp),
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PutEventPreview() {
        PutEvent(
            photoUrl = "",
            eventName = "ani partty",
            eventDate = "2023 - 11 - 2",
            eventTime = "10:30AM",
            eventAddress = "101 Erskine",
            eventDescription = "My party",
            eventAttendee = "",
            isInProgress = false,
            onEventNameChanged = {},
            onDescriptionChanged = {},
            onImageButtonClicked = {},
            onEventDateClicked = {},
            onEventTimeClicked = {},
            onEventLocationClicked = {},
            onEventAttendeeClicked = {},
            onEventActionButtonClicked = {},
        )
    }

    @Composable
    fun EventImage(
        photoUrl: String,
        size: Dp,
        modifier: Modifier
    ) {
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(data = photoUrl)
                .apply(block = fun ImageRequest.Builder.() {
                    placeholder(R.drawable.img_event)
                    error(R.drawable.img_event)
                }).build()
        )
        Card(colors = CardDefaults.cardColors(customBackgroundColor)) {
            Image(
                painter = painter,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = modifier
                    .fillMaxWidth()
                    .size(size)
            )
        }
    }
}