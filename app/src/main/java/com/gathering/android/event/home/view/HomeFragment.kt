package com.gathering.android.event.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.gathering.android.R
import com.gathering.android.databinding.FrgHomeBinding
import com.gathering.android.event.home.EventListViewModel
import com.gathering.android.event.home.view.FilterDialogFragment.Companion.TAG
import com.gathering.android.event.home.viewmodel.EventViewState
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
        adapter.setOnEventClickListener {
            viewModel.onEventItemClicked(it)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                EventViewState.HideNoData -> binding.tvNoData.isVisible = false
                EventViewState.HideProgress -> binding.prg.isVisible = false
                is EventViewState.NavigateToEventDetail -> {
                    val bundle = bundleOf("event" to state.event)
                    findNavController().navigate(
                        R.id.action_navigation_home_to_eventDetailFragment, bundle
                    )
                }

                is EventViewState.ShowError -> showToast(state.errorMessage)
                is EventViewState.ShowEventList -> {
                    adapter.setEventItem(state.eventList.toMutableList())
                }

                EventViewState.ShowNoData -> binding.tvNoData.isVisible = true
                EventViewState.ShowProgress -> binding.prg.isVisible = true
                EventViewState.NavigateToIntroScreen -> {
                    if (R.id.verificationFragment != findNavController().currentDestination?.id) {
                        findNavController().navigate(R.id.action_homeFragment_to_introFragment)
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvEvent.adapter = adapter
        binding.rvEvent.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        viewModel.onViewCreated()


        binding.sortButton.setOnClickListener {
            val dialog = SortDialogFragment(viewModel)
            dialog.show(parentFragmentManager, TAG)
        }

        binding.filterButton.setOnClickListener {
            val dialog = FilterDialogFragment(viewModel)
            dialog.show(parentFragmentManager, TAG)
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_LONG
        ).show()
    }
}
