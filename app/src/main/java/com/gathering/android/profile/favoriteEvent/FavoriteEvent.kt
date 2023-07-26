package com.gathering.android.profile.favoriteEvent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gathering.android.R
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.BottomSheetFavoriteEventBinding
import com.gathering.android.event.home.EndlessScrollListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteEvent : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetFavoriteEventBinding

    @Inject
    lateinit var viewModel: FavoriteEventViewModel

    @Inject
    lateinit var adapter: FavoriteEventAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenCustomBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetFavoriteEventBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvEvent.layoutManager = linearLayoutManager
        binding.rvEvent.adapter = adapter

        binding.rvEvent.addOnScrollListener(EndlessScrollListener {
            viewModel.onLastItemReached()
        })

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteEventViewState.AppendEventList -> {
                    adapter.appendEventItems(state.eventList)
                }

                FavoriteEventViewState.HideNoData -> {
                    binding.tvNoData.isVisible = true
                }

                FavoriteEventViewState.HideProgress -> {
                    binding.prg.isVisible = false
                }

                is FavoriteEventViewState.ShowError -> {
                    showErrorText(state.errorMessage)
                }

                is FavoriteEventViewState.ShowFavoriteEvent -> {
                    adapter.setEventItem(state.eventList.toMutableList())
                }

                FavoriteEventViewState.ShowNoData -> {
                    binding.tvNoData.isVisible = true
                }

                FavoriteEventViewState.ShowProgress -> {
                    binding.prg.isVisible = true
                }

                is FavoriteEventViewState.UpdateEvent -> {
                    adapter.updateEvent(state.event)
                }
            }
        }

        adapter.setOnFavoriteImageClick { event ->
            viewModel.onEventLikeClicked(event)
        }

        viewModel.onViewCreated()
    }
}