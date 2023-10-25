package com.gathering.android.event.myevent

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.R
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenMyEventBinding
import com.gathering.android.event.Event
import com.gathering.android.event.EventList
import com.gathering.android.event.KEY_ARGUMENT_EVENT
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
                        Surface(
                            modifier = Modifier.wrapContentSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val state = viewModel.uiState.collectAsState()

                            EventList(
                                showEditIcon = true,
                                showFavoriteIcon = false,
                                events = state.value.myEvents,
                                onItemClick = {},
                                onEditClick = { viewModel.onEditEventClicked(it) },
                                onFavClick = {},
                                isLoading = state.value.showProgress,
                                isNoData = state.value.showNoData,
                                onFabClick = viewModel::onFabButtonClicked,
                                onDeleteClick = { viewModel.onSwipedToDelete(it) },
                                onUndoDeleteEvent = {viewModel.onUndoDeleteEvent(it)}
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
                                binding.root, EVENT_DELETED, Snackbar.LENGTH_LONG
                            )
                            snackBar.setAction(UNDO) {
                                viewModel.onUndoDeleteEvent(event)
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

            getNavigationResultLiveData<Boolean>(KEY_ARGUMENT_UPDATE_MY_EVENT_LIST)?.observe(
                viewLifecycleOwner
            ) {
                viewModel.onEventAdded()
            }
            viewModel.onViewCreated(this)
        }

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

    companion object {
        private const val EVENT_DELETED = "EVENT DELETED"
        private const val UNDO = "UNDO"
    }
}