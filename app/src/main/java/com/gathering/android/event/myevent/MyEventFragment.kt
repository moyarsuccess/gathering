package com.gathering.android.event.myevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.R
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.FrgMyEventBinding
import com.gathering.android.event.KEY_ARGUMENT_EVENT
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_MY_EVENT_LIST
import com.gathering.android.home.EndlessScrollListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyEventFragment : Fragment() {

    private lateinit var binding: FrgMyEventBinding

    @Inject
    lateinit var adapter: MyEventAdapter

    @Inject
    lateinit var viewModel: MyEventViewModel

    private lateinit var swipeGesture: SwipeGesture
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FrgMyEventBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvEvent.layoutManager = linearLayoutManager

        swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val event = adapter.getEventAtPosition(position)
                event?.let {
                    if (direction == ItemTouchHelper.LEFT) {
                        viewModel.onDeleteEvent(it)
                        // Show a snack bar with an "Undo" option for the delete action
                        val snackBar = Snackbar.make(
                            binding.root, "Event deleted", Snackbar.LENGTH_LONG
                        )
                        snackBar.setAction("Undo") {
                            viewModel.onUndoDeleteEvent()
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

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                MyEventViewState.HideNoData -> binding.tvNoData.isVisible = false
                MyEventViewState.HideProgress -> binding.prg.isVisible = false
                MyEventViewState.NavigateToAddEvent -> view.let {
                    findNavController().navigate(R.id.action_navigation_eventFragment_to_putEventBottomSheetFragment)
                }

                is MyEventViewState.ShowError -> showErrorText(state.errorMessage)
                MyEventViewState.ShowProgress -> binding.prg.isVisible = true

                is MyEventViewState.ShowNextEventPage -> adapter.appendEventItems(state.myEventList.toMutableList())
                is MyEventViewState.UpdateEvent -> {
                    adapter.updateEvent(state.event)
                    binding.tvNoData.isVisible = adapter.itemCount == 0
                    updateUIAfterDeletion()
                }

                MyEventViewState.ShowNoData -> {
                    binding.tvNoData.isVisible = true
                    binding.rvEvent.isVisible = false
                }

                is MyEventViewState.NavigateToEditMyEvent -> {
                    val event = state.event
                    val bundle = bundleOf(KEY_ARGUMENT_EVENT to event)
                    findNavController().navigate(
                        R.id.action_navigation_eventFragment_to_putEventBottomSheetFragment,
                        bundle
                    )
                }

                is MyEventViewState.AppendEventList -> {
                    adapter.appendEventItems(state.eventList.toMutableList())
                }

                MyEventViewState.ClearData -> adapter.clearData()

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
        viewModel.onViewCreated()
    }

    private fun updateUIAfterDeletion() {
        val isListEmpty = adapter.itemCount == 0
        binding.tvNoData.isVisible = isListEmpty
    }
}