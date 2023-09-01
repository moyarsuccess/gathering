package com.gathering.android.auth.password.newPassword

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.auth.verification.VerificationScreen
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenNewPasswordInputBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InputNewPasswordScreen : FullScreenBottomSheet(), InputNewPasswordNavigator {

    private lateinit var binding: ScreenNewPasswordInputBinding

    @Inject
    lateinit var viewModel: InputNewPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ScreenNewPasswordInputBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setOnClickListener {
            val password = binding.etPassInput.text.toString()
            val confirmPassword = binding.etConfirmPassInput.text.toString()
            viewModel.onSubmitBtnClicked(extractToken(), password, confirmPassword)
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (state.isInProgress) {
                    binding.btnSubmit.startAnimation()
                } else {
                    binding.btnSubmit.revertAnimation()
                }
                state.errorMessage?.let {
                    showErrorText(it)
                }
            }
        }
        viewModel.onViewCreated(this)
    }

    private fun extractToken(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(NavController.KEY_DEEP_LINK_INTENT, Intent::class.java)
        } else {
            arguments?.getParcelable(NavController.KEY_DEEP_LINK_INTENT)
        }?.data?.getQueryParameter(VerificationScreen.TOKEN_PARAM)
    }

    override fun navigateToHomeFragment() {
        findNavController().navigate(
            R.id.action_newPasswordInputFragment_to_navigation_homeFragment
        )
    }
}