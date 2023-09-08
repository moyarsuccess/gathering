package com.gathering.android.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignUpScreen : DialogFragment(), SignUpNavigator {

    private lateinit var binding: ScreenSignUpBinding

    @Inject
    lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ScreenSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            val email = binding.etMail.text.toString()
            val password = binding.etPass.text.toString()
            val confirmPassword = binding.etVerifyPass.text.toString()
            viewModel.onSignUpButtonClicked(email, password, confirmPassword)
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (state.isInProgress) {
                    binding.btnSignUp.startAnimation()
                } else {
                    binding.btnSignUp.revertAnimation()
                }
                state.errorMessage?.let {
                    showErrorText(it)
                }
            }
        }
        viewModel.onViewCreated(this)
    }

    override fun navigateToVerification() {
        val bundle = Bundle()
        findNavController().navigate(
            R.id.action_signUpScreen_to_verificationScreen, bundle
        )
    }
}