package com.gathering.android.event.myevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.gathering.android.R
import com.gathering.android.common.getNavigationResultLiveData
import com.gathering.android.databinding.FrgMyEventBinding
import com.gathering.android.event.KEY_ARGUMENT_UPDATE_MY_EVENT_LIST
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyEventFragment : Fragment() {

    private lateinit var binding: FrgMyEventBinding

    @Inject
    lateinit var adapter: MyEventAdapter

    @Inject
    lateinit var viewModel: MyEventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FrgMyEventBinding.inflate(layoutInflater)

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                MyEventViewState.HideNoData -> binding.tvNoData.isVisible = false
                MyEventViewState.HideProgress -> binding.prg.isVisible = false
                MyEventViewState.NavigateToAddEvent -> view?.let {
                    findNavController().navigate(R.id.action_navigation_event_to_addEventBottomSheetFragment)
                }

                is MyEventViewState.ShowError -> showToast(state.errorMessage)
                MyEventViewState.ShowNoData -> binding.tvNoData.isVisible = true
                MyEventViewState.ShowProgress -> binding.prg.isVisible = true
                is MyEventViewState.ShowUserEventList -> adapter.setEventItem(state.myEventList.toMutableList())
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

        binding.btnFab.setOnClickListener {
            viewModel.onFabButtonClicked()
        }

        getNavigationResultLiveData<Boolean>(KEY_ARGUMENT_UPDATE_MY_EVENT_LIST)?.observe(
            viewLifecycleOwner
        ) {
            viewModel.onViewCreated()
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.onViewCreated()
    }

    private fun showToast(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }
}