package com.gathering.android.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gathering.android.R
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenHomeBinding
import com.gathering.android.event.EventList
import com.gathering.android.event.KEY_ARGUMENT_EVENT_ID
import com.gathering.android.home.FilterDialogFragment.Companion.TAG
import com.gathering.android.ui.theme.GatheringTheme
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

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenHomeBinding.inflate(layoutInflater)
            binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            modifier = Modifier.wrapContentSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val state = viewModel.uiState.collectAsState()
                            EventList(
                                showEditIcon = false,
                                showFavoriteIcon = true,
                                swipeEnabled = false,
                                noDataText = "oooops! No events yet.",
                                showSnackBar = false,
                                events = state.value.events,
                                showProgress = state.value.showProgress,
                                showNoData = state.value.showNoData,
                                onItemClick = viewModel::onEventItemClicked,
                                onNextPageRequested = viewModel::onNextPageRequested,
                                onFavClick = viewModel::onEventLikeClicked,
                                onFabClick = {},
                                onEditClick = {},
                                onSwipedToDelete = {},
                                onUndoClicked = {},
                                onSnackBarDismissed = {},
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isComposeEnabled) {
            viewModel.onViewCreated(this)
            return
        } else {
            setRecyclerViewAndInteractions()
        }
    }

    private fun setRecyclerViewAndInteractions() {
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
            viewModel.onEventItemClicked(it.eventId)
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

    override fun navigateToEventDetail(eventId: Long) {
        val bundle = bundleOf(KEY_ARGUMENT_EVENT_ID to eventId)
        findNavController().navigate(
            R.id.action_navigation_home_to_EventDetailScreen, bundle
        )
    }

    override fun navigateToIntroScreen() {
        if (R.id.verificationScreen != findNavController().currentDestination?.id &&
            R.id.newPasswordInputScreen != findNavController().currentDestination?.id
        ) {
            findNavController().navigate(R.id.action_homeScreen_to_introFragment)
        }
    }
}
