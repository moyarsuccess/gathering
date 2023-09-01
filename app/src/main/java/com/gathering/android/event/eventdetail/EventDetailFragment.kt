package com.gathering.android.event.eventdetail

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.ATTENDEE_LIST
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.FrgEventDetailBinding
import com.gathering.android.event.Event
import com.gathering.android.event.KEY_ARGUMENT_EVENT
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class EventDetailFragment : Fragment() {

    private lateinit var binding: FrgEventDetailBinding

    @Inject
    lateinit var viewModel: EventDetailViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FrgEventDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val event = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(KEY_ARGUMENT_EVENT, Event::class.java)
        } else {
            arguments?.getSerializable(KEY_ARGUMENT_EVENT) as Event
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EventDetailViewState.ShowError -> showErrorText(state.errorMessage)
                is EventDetailViewState.ShowEventDetail -> {
                    imageLoader.loadImage(state.event.photoUrl, binding.imgEvent)
                    binding.tvEventTitle.text = state.event.eventName
                    binding.tvEventHost.text = state.event.eventHostEmail
                    binding.tvEventDescription.text = state.event.description
                    //binding.tvEventAddress.text = "" // FIXME state.event.location?.addressLine
                    binding.tvEventDate.text =
                        SimpleDateFormat("EEEE, MMMM d, yyyy - h:mm a", Locale.getDefault()).format(
                            state.event.dateAndTime
                        )
                }
                is EventDetailViewState.NavigateToAttendeesDetailBottomSheet -> {
                    val bundle = bundleOf(ATTENDEE_LIST to state.attendees)
                    findNavController().navigate(
                        R.id.action_eventDetailFragment_to_attendeesDetailBottomSheet, bundle
                    )
                }
                EventDetailViewState.MaybeSelected -> {
                    binding.btnNo.setBackgroundResource(R.drawable.custom_button)
                    binding.btnYes.setBackgroundResource(R.drawable.custom_button)
                    binding.btnMaybe.setBackgroundColor(Color.GRAY)
                }
                EventDetailViewState.NoSelected -> {
                    binding.btnMaybe.setBackgroundResource(R.drawable.custom_button)
                    binding.btnYes.setBackgroundResource(R.drawable.custom_button)
                    binding.btnNo.setBackgroundColor(Color.GRAY)
                }
                EventDetailViewState.YesSelected -> {
                    binding.btnNo.setBackgroundResource(R.drawable.custom_button)
                    binding.btnMaybe.setBackgroundResource(R.drawable.custom_button)
                    binding.btnYes.setBackgroundColor(Color.GRAY)
                }
            }
        }

        event?.let {
            viewModel.onViewCreated(it)
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
    }
}