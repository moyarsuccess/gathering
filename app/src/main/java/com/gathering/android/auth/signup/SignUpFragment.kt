package com.gathering.android.auth.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
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
                is SignUpViewState.Error.ShowEmptyEmailError -> {
                    binding.etMail.error = state.errorMessage
                }

                is SignUpViewState.Error.ShowInvalidEmailError -> {
                    binding.etMail.error = state.errorMessage
                }

                is SignUpViewState.Error.ShowEmptyPassError -> {
                    binding.etPass.error = state.errorMessage
                }

                is SignUpViewState.Error.ShowInvalidPassError -> {
                    binding.etPass.error = state.errorMessage
                }

                is SignUpViewState.Error.ShowEmptyConfirmedPassError -> {
                    binding.etVerifyPass.error = state.errorMessage
                }

                is SignUpViewState.Error.ShowInvalidConfirmedPassError -> {
                    binding.etVerifyPass.error = state.errorMessage
                }

                is SignUpViewState.Error.ShowGeneralError -> {
                    showToast(state.errorMessage)
                }

                is SignUpViewState.SignUpButtonVisibility -> {
                    binding.btnSignUp.isEnabled = state.isSignUpButtonEnabled
                }

                is SignUpViewState.Error.ShowAuthenticationFailedError -> {
                    Log.d("WTF", state.errorMessage.toString())
                    showToast(state.errorMessage)
                }

                is SignUpViewState.NavigateToVerification -> {
                    val bundle = Bundle()
                    bundle.putString("email", state.email)
                    findNavController().navigate(
                        R.id.action_signUpFragment_to_showEmailVerification,
                        bundle
                    )
                }

                is SignUpViewState.Error.ShowSuccessWithError -> showToast(state.errorMessage)
            }
        }
    }

    private fun showToast(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }
}