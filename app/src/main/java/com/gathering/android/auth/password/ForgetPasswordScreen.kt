package com.gathering.android.auth.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenForgetPasswordEmailInputBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForgetPasswordScreen : FullScreenBottomSheet(), ForgetPasswordNavigator {

    private lateinit var binding: ScreenForgetPasswordEmailInputBinding

    @Inject
    lateinit var viewModel: ForgetPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ScreenForgetPasswordEmailInputBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (state.isInProgress) {
                    binding.btnSendLink.startAnimation()
                } else {
                    binding.btnSendLink.revertAnimation()
                }
                state.errorMessage?.let {
                    showErrorText(it)
                }

            }

        }

        binding.btnSendLink.setOnClickListener {
            val email = binding.etEmailInput.text.toString()
            viewModel.onSendLinkBtnClicked(email)
        }

        viewModel.onViewCreated(this)

    }

    override fun navigateToResetPassInfoBottomSheet() {
        findNavController().navigate(
            R.id.action_forgetPasswordFragment_to_resetPassInfoBottomSheet
        )
    }
}