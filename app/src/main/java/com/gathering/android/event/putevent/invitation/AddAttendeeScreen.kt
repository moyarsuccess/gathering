package com.gathering.android.event.putevent.invitation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.gathering.android.common.ATTENDEE_LIST
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.setNavigationResult
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.BottomSheetAddAttendeesBinding
import com.gathering.android.event.KEY_ARGUMENT_SELECTED_ATTENDEE_LIST
import com.gathering.android.event.putevent.invitation.viewModel.AddAttendeesViewModel
import com.gathering.android.event.putevent.invitation.viewModel.AddAttendeesViewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddAttendeeScreen : FullScreenBottomSheet() {

    private lateinit var binding: BottomSheetAddAttendeesBinding

    @Inject
    lateinit var adapter: AttendeeListAdapter

    @Inject
    lateinit var viewModel: AddAttendeesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddAttendeesBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                AddAttendeesViewState.HideKeyboard -> hideKeyboard()
                AddAttendeesViewState.CleaEditText -> binding.etAttendee.setText("")
                is AddAttendeesViewState.SetAttendee -> binding.etAttendee.setText(state.attendee)
                is AddAttendeesViewState.ShowError -> showErrorText(state.errorMessage)
                is AddAttendeesViewState.AddAttendeeToRecyclerView -> adapter.addAttendeeItem(state.attendee)
                is AddAttendeesViewState.RemoveAttendeeFromRecyclerView -> adapter.deleteAttendeeItem(
                    state.attendee
                )

                is AddAttendeesViewState.NavigateToAddEvent -> {
                    setNavigationResult(KEY_ARGUMENT_SELECTED_ATTENDEE_LIST, state.attendeeList)
                    findNavController().popBackStack()
                }

                is AddAttendeesViewState.AddAttendeeButtonVisibility -> binding.btnAddAttendee.isEnabled =
                    state.isAddAttendeeButtonEnabled
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
            val attendeeList = adapter.getAttendeeItems()
            viewModel.onOKButtonClicked(attendeeList)
        }

        binding.btnAddAttendee.setOnClickListener {
            viewModel.onAddAttendeeButtonClicked(binding.etAttendee.text.toString())
        }


        val attendees = arguments?.getString(ATTENDEE_LIST)
        viewModel.onViewCreated(attendees)

    }

    private fun hideKeyboard() {
        val inputSystemService = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
        val imm = inputSystemService as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}