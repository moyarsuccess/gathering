package com.gathering.android.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gathering.android.R
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.FrgHomeBinding
import com.gathering.android.event.KEY_ARGUMENT_EVENT
import com.gathering.android.home.FilterDialogFragment.Companion.TAG
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FrgHomeBinding

    @Inject
    lateinit var adapter: EventListAdapter

    @Inject
    lateinit var viewModel: EventListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FrgHomeBinding.inflate(layoutInflater)
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
                EventViewState.HideNoData -> binding.tvNoData.isVisible = false
                EventViewState.HideProgress -> binding.prg.isVisible = false
                is EventViewState.NavigateToEventDetail -> {
                    val bundle = bundleOf(KEY_ARGUMENT_EVENT to state.event)
                    findNavController().navigate(
                        R.id.action_navigation_home_to_EventDetailScreen, bundle
                    )
                }

                is EventViewState.ShowError -> showErrorText(state.errorMessage)
                is EventViewState.ShowEventList -> {
                    adapter.setEventItem(state.eventList.toMutableList())
                }

                is EventViewState.AppendEventList -> {
                    adapter.appendEventItems(state.eventList.toMutableList())
                }

                EventViewState.ShowNoData -> binding.tvNoData.isVisible = true
                EventViewState.ShowProgress -> binding.prg.isVisible = true
                EventViewState.NavigateToIntroScreen -> {
                    // TODO: we need to find a better way to handle this
                    if (R.id.verificationScreen != findNavController().currentDestination?.id &&
                        R.id.newPasswordInputFragment != findNavController().currentDestination?.id
                    ) {
                        findNavController().navigate(R.id.action_homeFragment_to_introFragment)
                    }
                }

                is EventViewState.UpdateEvent -> {
                    adapter.updateEvent(state.event)
                }
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

        viewModel.onViewCreated()
    }
}
