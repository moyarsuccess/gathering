package com.gathering.android.event.eventdetail

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gathering.android.common.ImageLoader
import com.gathering.android.databinding.FrgEventDetailBinding
import com.gathering.android.event.Event
import com.gathering.android.event.KEY_ARGUMENT_EVENT
import dagger.hilt.android.AndroidEntryPoint
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
                is EventDetailViewState.ShowError -> showToast(state.errorMessage)
                is EventDetailViewState.ShowEventDetail -> {
                    imageLoader.loadImage(state.event.photoUrl, binding.imgEvent)
                    binding.tvEventTitle.text = state.event.eventName
                    binding.tvEventHost.text = state.event.eventHostEmail.toString()
                    binding.tvEventDescription.text = state.event.description
                    binding.tvEventAddress.text = state.event.location.addressLine
                    binding.tvEventDate.text = state.event.dateAndTime.toString()
                }
            }
        }

        event?.also { event ->
            viewModel.onViewCreated(event)
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_LONG
        ).show()
    }
}