package com.gathering.android.auth.signin

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
import com.gathering.android.databinding.FrgSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : DialogFragment() {

    private lateinit var binding: FrgSignInBinding

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
        binding = FrgSignInBinding.inflate(layoutInflater)
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

        binding.btnSignIn.setOnClickListener {
            val email = binding.etMail.text.toString()
            val pass = binding.etPass.text.toString()
            viewModel.onSignInButtonClicked(email, pass)
        }

        binding.tvForgetPassword.setOnClickListener {
            viewModel.onForgotPassTvClicked()
        }


        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SignInViewState.Error.ShowEmptyEmailError -> {
                    binding.etMail.error = state.errorMessage
                }

                is SignInViewState.Error.ShowInvalidEmailError -> {
                    binding.etMail.error = state.errorMessage
                }

                is SignInViewState.Error.ShowEmptyPassError -> {
                    binding.etPass.error = state.errorMessage
                }

                is SignInViewState.Error.ShowInvalidPassError -> {
                    binding.etPass.error = state.errorMessage
                }

                is SignInViewState.Error.ShowGeneralError -> {
                    showToast(state.errorMessage)
                }

                is SignInViewState.SignInButtonVisibility -> {
                    binding.btnSignIn.isEnabled = state.isSignInButtonEnabled
                }

                is SignInViewState.Error.ShowAuthenticationFailedError -> {
                    Log.d("WTF", state.errorMessage.toString())
                    showToast(state.errorMessage)
                }

                SignInViewState.NavigateToVerification -> {
                    findNavController().navigate(
                        R.id.action_signInFragment_to_verificationFragment
                    )
                }

                SignInViewState.NavigateToHome -> {
                    findNavController().navigate(
                        R.id.action_signInFragment_to_navigation_homeFragment
                    )
                }

                SignInViewState.NavigateToPasswordReset -> {
                    findNavController().navigate(
                        R.id.action_signInFragment_to_forgetPasswordFragment
                    )
                }

                is SignInViewState.Error.ShowUserNotVerifiedError -> showToast(state.errorMessage)
            }
        }
    }

    private fun showToast(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }
}
