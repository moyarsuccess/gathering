package com.gathering.android.event.eventdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.gathering.android.common.ATTENDEE_LIST
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.databinding.BottomSheetAttendeesDetailBinding
import com.gathering.android.event.model.Attendee
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AttendeesDetailBottomSheet : FullScreenBottomSheet() {

    private lateinit var binding: BottomSheetAttendeesDetailBinding

    @Inject
    lateinit var adapter: AttendeesDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAttendeesDetailBinding.inflate(LayoutInflater.from(requireContext()))

        val attendees = arguments?.getSerializable(ATTENDEE_LIST) as List<Attendee>

        adapter.setItems(attendees)
        binding.rvAttendees.adapter = adapter
        binding.rvAttendees.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)


        binding.btnYes.setOnClickListener {
            adapter.setCurrentAcceptType(AcceptType.Yes)
            updateNoAttendeesTextView()
        }

        binding.btnNo.setOnClickListener {
            adapter.setCurrentAcceptType(AcceptType.No)
            updateNoAttendeesTextView()
        }

        binding.btnMaybe.setOnClickListener {
            adapter.setCurrentAcceptType(AcceptType.Maybe)
            updateNoAttendeesTextView()
        }

        updateNoAttendeesTextView()

        return binding.root
    }

    private fun updateNoAttendeesTextView() {
        if (adapter.itemCount == 0) {
            binding.noData.visibility = View.VISIBLE
        } else {
            binding.noData.visibility = View.GONE
        }
    }
}