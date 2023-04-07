package com.gathering.android.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.databinding.FrgSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : DialogFragment() {

    private lateinit var binding: FrgSignUpBinding

    @Inject
    lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Light_NoTitleBar_Fullscreen
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FrgSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etMail.doOnTextChanged { text, _, _, _ ->
            viewModel.onEmailAddressChanged(text.toString())
        }

        binding.etPass.doOnTextChanged { text, _, _, _ ->
            viewModel.onPasswordChanged(text.toString())
        }

        binding.etVerifyPass.doOnTextChanged { text, _, _, _ ->
            val pass = binding.etPass.text.toString()
            val confirmedPass = text.toString()
            viewModel.onConfirmedPasswordChanged(pass, confirmedPass)
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.etMail.text.toString()
            val pass = binding.etPass.text.toString()
            viewModel.onSignUpButtonClicked(email, pass)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SignUpScreenViewState.Error.ShowEmptyEmailError -> {
                    binding.etMail.error = state.errorMessage
                }
                is SignUpScreenViewState.Error.ShowInvalidEmailError -> {
                    binding.etMail.error = state.errorMessage
                }
                is SignUpScreenViewState.Error.ShowEmptyPassError -> {
                    binding.etPass.error = state.errorMessage
                }
                is SignUpScreenViewState.Error.ShowInvalidPassError -> {
                    binding.etPass.error = state.errorMessage
                }
                is SignUpScreenViewState.Error.ShowEmptyConfirmedPassError -> {
                    binding.etVerifyPass.error = state.errorMessage
                }
                is SignUpScreenViewState.Error.ShowInvalidConfirmedPassError -> {
                    binding.etVerifyPass.error = state.errorMessage
                }
                is SignUpScreenViewState.Error.ShowGeneralError -> {
                    showToast(state.errorMessage)
                }
                is SignUpScreenViewState.NavigateToHomeScreen -> {
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                }
                is SignUpScreenViewState.SignUpButtonVisibility -> {
                    binding.btnSignUp.isEnabled = state.isSignUpButtonEnabled
                }
                is SignUpScreenViewState.Error.ShowAuthenticationFailedError -> {
                    showToast(state.errorMessage)
                }
            }
        }
    }

    private fun showToast(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }
}