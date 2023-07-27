package com.gathering.android.event.myevent

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gathering.android.R
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.FrgMyEventBinding
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_MY_EVENT_LIST
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

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {

        // this allows you to customize the appearance of the RecyclerView item while it is being swiped
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
            val editIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit)

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                val itemView = viewHolder.itemView

                val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                val iconBottom = iconTop + deleteIcon.intrinsicHeight

                if (dX > 0) {
                    // Swiping right (edit action)
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = itemView.left + iconMargin + editIcon!!.intrinsicWidth

                    editIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    editIcon.draw(c)
                } else if (dX < 0) {
                    // Swiping left (delete action)
                    val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin

                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteIcon.draw(c)
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        //The onMove method is called when the user drags an item from its current position to another position in the RecyclerView.
        // It provides you with the opportunity to update the data list and notify the adapter about the item's new position
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val event = adapter.getEventAtPosition(position)
            event?.let {
                if (direction == ItemTouchHelper.LEFT) {
                    // Handle swipe to delete
                    viewModel.onDeleteEvent(it)
                    // Show a snackbar with an "Undo" option for the delete action
                    val snackbar = Snackbar.make(
                        binding.root, "Event deleted", Snackbar.LENGTH_LONG
                    )
                    snackbar.setAction("Undo") {
                        viewModel.onUndoDeleteEvent()
                    }
                    snackbar.show()
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Handle swipe to edit
                    viewModel.onEditEvent(it)
                }

            }
        }


        //By specifying the swipe directions, you can control whether the user can swipe left, right, up, or down to trigger a particular action, such as delete or edit.
        override fun getSwipeDirs(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
        ): Int {
            return if (viewHolder is MyEventAdapter.ViewHolder) {
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            } else {
                super.getSwipeDirs(recyclerView, viewHolder)
            }
        }
    }
    private val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FrgMyEventBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvEvent.adapter = adapter
        itemTouchHelper.attachToRecyclerView(binding.rvEvent)
        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                MyEventViewState.HideNoData -> binding.tvNoData.isVisible = false
                MyEventViewState.HideProgress -> binding.prg.isVisible = false
                MyEventViewState.NavigateToAddEvent -> view?.let {
                    findNavController().navigate(R.id.action_navigation_event_to_addEventBottomSheetFragment)
                }

                is MyEventViewState.ShowError -> showErrorText(state.errorMessage)
                MyEventViewState.ShowProgress -> binding.prg.isVisible = true
                is MyEventViewState.ShowUserEventList -> {
                    binding.tvNoData.isVisible = state.myEventList.isEmpty()
                    adapter.setEventItem(state.myEventList.toMutableList())
                }

                is MyEventViewState.UpdateEvent -> {
                    adapter.updateEvent(state.event)
                    adapter.deleteEvent(state.event)
                    binding.tvNoData.isVisible = adapter.itemCount == 0
                    updateUIAfterDeletion()
                }
                MyEventViewState.ShowNoData -> {
                    binding.tvNoData.isVisible = true
                    binding.rvEvent.isVisible = false
                }
                MyEventViewState.NavigateToEditMYEvent -> {
                    findNavController().navigate(
                        R.id.action_navigation_event_to_editMyEventBottomSheet
                    )
                }
            }
        }

        binding.btnFab.setOnClickListener {
            viewModel.onFabButtonClicked()
        }

        adapter.setOnFavoriteImageClick { event ->
            viewModel.onEventLikeClicked(event)
        }

        getNavigationResultLiveData<Boolean>(KEY_ARGUMENT_UPDATE_MY_EVENT_LIST)?.observe(
            viewLifecycleOwner
        ) {
            viewModel.onViewCreated()
        }
    }

    //updating the UI after an item is deleted
    private fun updateUIAfterDeletion() {
        val isListEmpty = adapter.itemCount == 0
        binding.tvNoData.isVisible = isListEmpty
    }

    override fun onResume() {
        super.onResume()
        viewModel.onViewCreated()
    }
}