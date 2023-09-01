package com.gathering.android.event.eventdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gathering.android.R
import com.gathering.android.common.ATTENDEE_LIST
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.databinding.BottomSheetAttendeesDetailBinding
import com.gathering.android.event.model.Attendee
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AttendeesDetailScreen : FullScreenBottomSheet() {

    private lateinit var binding: BottomSheetAttendeesDetailBinding

    @Inject
    lateinit var adapter: AttendeesDetailAdapter

    @Inject
    lateinit var viewModel: AttendeesDetailViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAttendeesDetailBinding.inflate(LayoutInflater.from(requireContext()))
        binding.rvAttendees.adapter = adapter
        binding.rvAttendees.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->

                binding.btnYes.setBackgroundResource(R.drawable.custom_button)
                binding.btnNo.setBackgroundResource(R.drawable.custom_button)
                binding.btnMaybe.setBackgroundResource(R.drawable.custom_button)

                when (uiState.selectedAcceptType) {
                    AcceptType.Yes -> {
                        binding.btnYes.setBackgroundResource(R.color.gray)
                    }
                    AcceptType.Maybe -> {
                        binding.btnMaybe.setBackgroundResource(R.color.gray)
                    }
                    AcceptType.No -> {
                        binding.btnNo.setBackgroundResource(R.color.gray)
                    }
                }
                adapter.setItems(uiState.selectedAttendeesList)

                if (uiState.showNoData) {
                    binding.noData.visibility = View.VISIBLE
                } else {
                    binding.noData.visibility = View.GONE
                }
            }
        }

        val attendees = arguments?.getSerializable(ATTENDEE_LIST) as List<Attendee>
        viewModel.onViewCreated(attendees)

        binding.btnYes.setOnClickListener {
            viewModel.onAcceptTypeSelectionChanged(AcceptType.Yes)
        }

        binding.btnNo.setOnClickListener {
            viewModel.onAcceptTypeSelectionChanged(AcceptType.No)
        }

        binding.btnMaybe.setOnClickListener {
            viewModel.onAcceptTypeSelectionChanged(AcceptType.Maybe)
        }
        return binding.root
    }
}