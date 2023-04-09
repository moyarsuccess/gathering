package com.gathering.android.auth.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.databinding.FrgSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var binding: FrgSignInBinding

    @Inject
    lateinit var viewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        binding.signInBtn.setOnClickListener {
            val email = binding.etMail.text.toString()
            val pass = binding.etPass.text.toString()
            viewModel.onSignInButtonClicked(email, pass)
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
                is SignInViewState.NavigateToEventScreen -> {
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
                is SignInViewState.SignInButtonVisibility -> {
                    binding.signInBtn.isEnabled = state.isSignInButtonEnabled
                }
                is SignInViewState.Error.ShowAuthenticationFailedError -> {
                    showToast(state.errorMessage)
                }

            }
        }
    }

    private fun showToast(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }
}
