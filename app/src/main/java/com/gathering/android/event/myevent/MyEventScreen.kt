package com.gathering.android.event.myevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.R
import com.gathering.android.common.composables.SnackbarSupportedSurface
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenMyEventBinding
import com.gathering.android.event.Event
import com.gathering.android.event.EventList
import com.gathering.android.event.KEY_ARGUMENT_EVENT
import com.gathering.android.event.KEY_ARGUMENT_EVENT_ID
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_MY_EVENT_LIST
import com.gathering.android.home.EndlessScrollListener
import com.gathering.android.ui.theme.GatheringTheme
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyEventScreen : Fragment(), MyEventNavigator {

    private lateinit var binding: ScreenMyEventBinding

    @Inject
    lateinit var adapter: MyEventAdapter

    @Inject
    lateinit var viewModel: MyEventViewModel

    private lateinit var swipeGesture: SwipeGesture
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenMyEventBinding.inflate(layoutInflater)
            return binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        SnackbarSupportedSurface(
                            modifier = Modifier.wrapContentSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val state = viewModel.uiState.collectAsState().value
                            println("WTF: 2 ${state.myEvents.size}")
                            EventList(
                                showEditIcon = true,
                                swipeEnabled = true,
                                noDataText = "You don't have any events yet! click on + button to add your event!",
                                showFavoriteIcon = false,
                                deletedEventName = state.deletedEventName ?: "",
                                showAddFab = true,
                                events = state.myEvents,
                                showProgress = state.showProgress,
                                showNoData = state.showNoData,
                                showSnackBar = state.showSnackBar,
                                onItemClick = { viewModel.onEventItemClicked(it) },
                                onEditClick = { viewModel.onEditEventClicked(it) },
                                onNextPageRequested = viewModel::onNextPageRequested,
                                onFabClick = viewModel::onFabButtonClicked,
                                onFavClick = {},
                                onSwipedToDelete = { viewModel.onSwipedToDelete(it) },
                                onUndoClicked = { viewModel.onUndoClicked() },
                                onSnackBarDismissed = { viewModel.onSnackBarDismissed() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getNavigationResultLiveData<Boolean>(KEY_ARGUMENT_UPDATE_MY_EVENT_LIST)?.observe(
            viewLifecycleOwner
        ) {
            viewModel.onEventAdded()
        }
        if (isComposeEnabled) {
            viewModel.onViewCreated(this)
            return
        } else {
            setupRecyclerViewAndInteractions()
        }
    }

    private fun setupRecyclerViewAndInteractions() {
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvEvent.layoutManager = linearLayoutManager

        swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val event = adapter.getEventAtPosition(position)
                event?.let {
                    if (direction == ItemTouchHelper.LEFT) {
                        viewModel.onSwipedToDelete(it)
                        // Show a snack bar with an "Undo" option for the delete action
                        val snackBar = Snackbar.make(
                            binding.root, EVENT_DELETED, Snackbar.LENGTH_SHORT
                        )
                        snackBar.setAction(UNDO) {
                            viewModel.onUndoClicked()
                        }
                        snackBar.show()
                    }
                }
            }
        }

        itemTouchHelper = ItemTouchHelper(swipeGesture)

        binding.rvEvent.adapter = adapter
        binding.rvEvent.addOnScrollListener(EndlessScrollListener {
            viewModel.onNextPageRequested()
        })
        itemTouchHelper.attachToRecyclerView(binding.rvEvent)


        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.prg.isVisible = state.showProgress

                state.errorMessage?.let {
                    showErrorText(it)
                }
                binding.tvNoData.isVisible = state.showNoData
                adapter.updateEvents(state.myEvents)
            }
        }
        binding.btnFab.setOnClickListener {
            viewModel.onFabButtonClicked()
        }

        adapter.setOnFavoriteImageClick { event ->
            viewModel.onEventLikeClicked(event)
        }

        adapter.setOnEditImageClick { event ->
            viewModel.onEditEventClicked(event)
        }

        viewModel.onViewCreated(this)
    }

    override fun navigateToAddEvent() {
        findNavController().navigate(
            R.id.action_navigation_eventFragment_to_putEventBottomSheetFragment
        )
    }

    override fun navigateToEditEvent(event: Event) {
        val bundle = bundleOf(KEY_ARGUMENT_EVENT to event)
        findNavController().navigate(
            R.id.action_navigation_eventFragment_to_putEventBottomSheetFragment, bundle
        )
    }

    override fun navigateToConfirmedAttendeesScreen(eventId: Long) {
        val bundle = bundleOf(KEY_ARGUMENT_EVENT_ID to eventId)
        findNavController().navigate(
            R.id.action_navigation_eventFragment_to_rsvpListScreen, bundle
        )
    }

    companion object {
        private const val EVENT_DELETED = "EVENT DELETED"
        private const val UNDO = "UNDO"
    }
}