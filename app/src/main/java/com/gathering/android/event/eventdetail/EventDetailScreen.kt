package com.gathering.android.event.eventdetail

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.ATTENDEE_LIST
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenEventDetailBinding
import com.gathering.android.event.KEY_ARGUMENT_EVENT_ID
import com.gathering.android.event.model.AttendeeModel
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EventDetailScreen : Fragment(), EventDetailNavigator {

    private lateinit var binding: ScreenEventDetailBinding

    @Inject
    lateinit var viewModel: EventDetailViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenEventDetailBinding.inflate(layoutInflater)
            return binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            viewModel
                                .uiState
                                .collectAsState()
                                .value
                                .apply {
                                    EventDetail(
                                        eventName = eventName ?: "",
                                        eventHostEmail = hostEvent ?: "",
                                        description = eventDescription ?: "",
                                        photoUrl = imageUri ?: "",
                                        address = eventAddress ?: "",
                                        date = eventDate ?: "",
                                        time = eventTime ?: "",
                                        acceptType = acceptType,
                                        errorMessage = errorMessage ?: "",
                                        onYesButtonClick = viewModel::onYesButtonClicked,
                                        onNoButtonClick = viewModel::onNoButtonClicked,
                                        onMaybeButtonClick = viewModel::onMaybeButtonClicked,
                                        onAttachListClicked = viewModel::onTvAttendeesDetailsClicked,
                                    )
                                }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getLong(KEY_ARGUMENT_EVENT_ID)
        } else {
            arguments?.getLong(KEY_ARGUMENT_EVENT_ID)
        }
        if (isComposeEnabled) {
            viewModel.onViewCreated(eventId, this)
            return
        } else {
            setUpUi(eventId)
        }
    }

    private fun setUpUi(eventId: Long?) {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                imageLoader.loadImage(state.imageUri, binding.imgEvent)
                binding.tvEventTitle.text = state.eventName
                binding.tvEventHost.text = state.hostEvent
                binding.tvEventDescription.text = state.eventDescription
                binding.tvEventDate.text = getString(
                    R.string.event_detail_date_and_time,
                    state.eventDate,
                    state.eventTime,
                )
                binding.tvEventAddress.text = state.eventAddress
                if (!state.errorMessage.isNullOrEmpty()) {
                    showErrorText(state.errorMessage)
                }
                if (state.acceptButtonBackColor == R.color.gray) {
                    binding.btnYes.setBackgroundColor(Color.GRAY)
                    binding.btnNo.setBackgroundResource(R.drawable.custom_button)
                    binding.btnMaybe.setBackgroundResource(R.drawable.custom_button)
                }

                if (state.declineButtonBackColor == R.color.gray) {
                    binding.btnNo.setBackgroundColor(Color.GRAY)
                    binding.btnYes.setBackgroundResource(R.drawable.custom_button)
                    binding.btnMaybe.setBackgroundResource(R.drawable.custom_button)
                }

                if (state.maybeButtonBackColor == R.color.gray) {
                    binding.btnMaybe.setBackgroundColor(Color.GRAY)
                    binding.btnYes.setBackgroundResource(R.drawable.custom_button)
                    binding.btnNo.setBackgroundResource(R.drawable.custom_button)
                }
            }
        }

        binding.btnYes.setOnClickListener {
            viewModel.onYesButtonClicked()
        }

        binding.btnNo.setOnClickListener {
            viewModel.onNoButtonClicked()
        }

        binding.btnMaybe.setOnClickListener {
            viewModel.onMaybeButtonClicked()
        }

        binding.tvAttendeesCount.setOnClickListener {
            viewModel.onTvAttendeesDetailsClicked()
        }


        viewModel.onViewCreated(eventId, this)
    }

    override fun navigateToAttendeesDetail(attendeeModels: List<AttendeeModel>) {
        val bundle = bundleOf(ATTENDEE_LIST to attendeeModels)
        findNavController().navigate(
            R.id.action_EventDetailScreen_to_attendeesDetailScreen,
            bundle
        )
    }
}
