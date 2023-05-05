package com.gathering.android.event.myevent.addevent

import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.databinding.BottomSheetAddEventBinding
import com.gathering.android.event.home.model.Event
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AddEventBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetAddEventBinding

    @Inject
    lateinit var viewModel: AddEventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddEventBinding.inflate(LayoutInflater.from(requireContext()))

        val selectedImagePath = arguments?.getString(KEY_ARGUMENT_SELECTED_IMAGE)


        if(selectedImagePath != null) {
            binding.imgEvent.setImageURI(Uri.parse(selectedImagePath))
        }

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
                AddEventViewState.NavigateToInviteFriend -> {
                    findNavController().navigate(R.id.action_addEventBottomSheetFragment_to_inviteFriendBottomSheet)
                }
                is AddEventViewState.NavigateToMyEvent -> {
                    findNavController().navigate(R.id.action_addEventBottomSheetFragment_to_navigation_event)
                }
                is AddEventViewState.ShowError -> showToast(state.errorMessage)
                is AddEventViewState.SetAddress -> binding.tvAddress.setText(state.address)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLocation.setOnClickListener {
            viewModel.onLocationButtonClicked()
        }

        binding.imgEvent.setOnClickListener {
            viewModel.onImageButtonClicked()
        }

        binding.imgInvitation.setOnClickListener {
            viewModel.onInvitationButtonClicked()
        }

        binding.btnAddEvent.setOnClickListener {
            //TODO:
            //1- upload the image
            //2- save the image url
            viewModel.onAddEventButtonClicked(provideEvent())
        }

        getNavigationResultLiveData<String>()?.observe(viewLifecycleOwner) {
            viewModel.onAddressChanged(it)
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
            eventName = "Birthday party",
            hostName = "Ani",
            description = binding.etDescription.toString(),
            photoUrl = "",
            location = Location("").also { it.altitude = 45.5019; it.longitude = 73.5674 },
            startTime = "19:00",
            endTime = "12:00",
            date = Date(),
            isContactEvent = false,
            isMyEvent = true,
            activities = listOf("party", "dinner", "drink", "cake", "tea")
        )
    }

    companion object {
        const val KEY_ARGUMENT_SELECTED_IMAGE = "selectedImagePath"
    }
}