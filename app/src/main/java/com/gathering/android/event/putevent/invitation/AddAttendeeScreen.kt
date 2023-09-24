package com.gathering.android.event.putevent.invitation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.gathering.android.common.ATTENDEE_LIST
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.setNavigationResult
import com.gathering.android.databinding.ScreenAddAttendeesBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ATTENDEE_LIST
import com.gathering.android.event.putevent.invitation.viewModel.AddAttendeesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddAttendeeScreen : FullScreenBottomSheet(), AddAttendeeNavigator {

    private lateinit var binding: ScreenAddAttendeesBinding

    @Inject
    lateinit var adapter: AttendeeListAdapter

    @Inject
    lateinit var viewModel: AddAttendeesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ScreenAddAttendeesBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                adapter.updateAttendeeItems(state.attendeesEmailList)

                binding.btnAddAttendee.isEnabled = state.addAttendeeButtonEnable
            }
        }

        adapter.setOnAttendeeRemoveListener {
            viewModel.onAttendeeRemoveItemClicked(it)
        }

        binding.rvContact.adapter = adapter
        binding.rvContact.addItemDecoration(
            DividerItemDecoration(
                context, DividerItemDecoration.HORIZONTAL
            )
        )

        binding.etAttendee.doOnTextChanged { text, _, _, _ ->
            viewModel.onAttendeeEmailChanged(text.toString())
        }

        binding.btnOk.setOnClickListener {
            viewModel.onOKButtonClicked()
        }

        binding.btnAddAttendee.setOnClickListener {
            viewModel.onAddAttendeeButtonClicked(binding.etAttendee.text.toString())
            hideKeyboard()
        }


        val attendees = arguments?.getString(ATTENDEE_LIST)
        viewModel.onViewCreated(attendees = attendees, addAttendeeNavigator = this)

    }

    private fun hideKeyboard() {
        val inputSystemService = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
        val imm = inputSystemService as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun navigateToAddEvent(attendeesEmailList: List<String>) {
        setNavigationResult(KEY_ARGUMENT_SELECTED_ATTENDEE_LIST, attendeesEmailList)
        findNavController().popBackStack()
    }
}