package com.gathering.android.auth.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignInScreen : DialogFragment(), SignInNavigator {

    private lateinit var binding: ScreenSignInBinding

    @Inject
    lateinit var viewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ScreenSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etMail.text.toString()
            val pass = binding.etPass.text.toString()
            viewModel.onSignInButtonClicked(email, pass)
        }

        binding.tvForgetPassword.setOnClickListener {
            viewModel.onForgotPassTvClicked()
        }


        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (state.isInProgress) {
                    binding.btnSignIn.startAnimation()
                } else {
                    binding.btnSignIn.revertAnimation()
                }
                state.errorMessage?.let {
                    showErrorText(it)
                }
            }
        }
        viewModel.onViewCreated(this)
    }

    override fun navigateToHome() {
        findNavController().navigate(
            R.id.action_signInScreen_to_navigation_homeFragment
        )
    }

    override fun navigateToVerification() {
        findNavController().navigate(
            R.id.action_signInScreen_to_verificationScreen
        )
    }

    override fun navigateToPasswordReset() {
        findNavController().navigate(
            R.id.action_signInScreen_to_forgetPasswordScreen
        )
    }
}
