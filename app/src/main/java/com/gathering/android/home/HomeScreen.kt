package com.gathering.android.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gathering.android.R
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenHomeBinding
import com.gathering.android.event.Event
import com.gathering.android.event.KEY_ARGUMENT_EVENT
import com.gathering.android.home.FilterDialogFragment.Companion.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeScreen : Fragment(), HomeNavigator {

    private lateinit var binding: ScreenHomeBinding

    @Inject
    lateinit var adapter: HomeEventsAdapter

    @Inject
    lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ScreenHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvEvent.layoutManager = linearLayoutManager
        binding.rvEvent.adapter = adapter

        binding.rvEvent.addOnScrollListener(EndlessScrollListener {
            viewModel.onNextPageRequested()
        })

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.prg.isVisible = state.showProgress

                state.errorMessage?.let {
                    showErrorText(it)
                }
                binding.tvNoData.isVisible = state.showNoData
                adapter.updateEvents(state.events)
            }
        }
        adapter.setOnEventClickListener {
            viewModel.onEventItemClicked(it)
        }

        adapter.setOnFavoriteImageClick { event ->
            viewModel.onEventLikeClicked(event)
        }

        binding.sortButton.setOnClickListener {
            val dialog = SortDialogFragment(viewModel)
            dialog.show(parentFragmentManager, TAG)
        }

        binding.filterButton.setOnClickListener {
            val dialog = FilterDialogFragment(viewModel)
            dialog.show(parentFragmentManager, TAG)
        }

        viewModel.onViewCreated(this)
    }

    override fun navigateToEventDetail(event: Event) {
        val bundle = bundleOf(KEY_ARGUMENT_EVENT to event)
        findNavController().navigate(
            R.id.action_navigation_home_to_EventDetailScreen, bundle
        )
    }

    override fun navigateToIntroScreen() {
        if (R.id.verificationScreen != findNavController().currentDestination?.id && R.id.newPasswordInputScreen != findNavController().currentDestination?.id) {
            findNavController().navigate(R.id.action_homeScreen_to_introFragment)
        }
    }
}