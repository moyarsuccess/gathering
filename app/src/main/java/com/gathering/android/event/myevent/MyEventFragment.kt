package com.gathering.android.event.myevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.databinding.FrgMyEventBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MyEventFragment : Fragment() {

    private lateinit var binding: FrgMyEventBinding

    @Inject
    lateinit var viewModel: MyEventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FrgMyEventBinding.inflate(layoutInflater)

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                MyEventViewState.HideNoData -> TODO()
                MyEventViewState.HideProgress -> TODO()
                MyEventViewState.NavigateToAddEvent -> view?.let {
                    findNavController().navigate(R.id.action_navigation_event_to_addEventBottomSheetFragment)
                }
                is MyEventViewState.ShowError -> TODO()
                MyEventViewState.ShowNoData -> TODO()
                MyEventViewState.ShowProgress -> TODO()
                MyEventViewState.ShowUserEventList -> TODO()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFab.setOnClickListener {
            viewModel.onFabButtonClicked()
        }
    }
}