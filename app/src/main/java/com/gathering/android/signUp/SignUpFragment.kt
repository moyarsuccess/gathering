package com.gathering.android.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.databinding.FrgSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FrgSignUpBinding

    @Inject
    lateinit var viewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                is SignUpScreenViewState.NavigateToEventScreen -> {
                    val bundle = Bundle()
                    bundle.putString("uId", state.user.uId.toString())
                    bundle.putString("displayName", state.user.displayName.toString())
                    bundle.putString("phoneNumber", state.user.phoneNumber.toString())
                    bundle.putString("photoUrl", state.user.photoUrl.toString())
                    state.user.isEmailVerified?.let { bundle.putBoolean("isEmailVerified", it) }
                    findNavController().navigate(
                        R.id.action_signUpFragment_to_eventFragment,
                        bundle
                    )
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