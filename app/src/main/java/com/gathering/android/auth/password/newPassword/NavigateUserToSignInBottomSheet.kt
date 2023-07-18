package com.gathering.android.auth.password.newPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.databinding.BottomSheetNavigateUserToSignInBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class NavigateUserToSignInBottomSheet : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModel: NavigateUserToSignInViewModel

    private lateinit var binding: BottomSheetNavigateUserToSignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.CustomBottomSheetDialogTheme
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetNavigateUserToSignInBinding.inflate(
            LayoutInflater.from(requireContext())
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackToSignIn.setOnClickListener {
            viewModel.onBackToSignInBtnClicked()
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                NavigateUserToSignInViewState.NavigateToSignInPage -> {
                    findNavController().navigate(
                        R.id.action_navigateUserToSignInBottomSheet_to_signInFragment
                    )                }
            }
        }

    }
}