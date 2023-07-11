package com.gathering.android.auth.verification

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.databinding.FrgVerificationBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VerificationFragment : DialogFragment() {

    private lateinit var binding: FrgVerificationBinding

    @Inject
    lateinit var viewModel: VerificationViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FrgVerificationBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendEmail.setOnClickListener {
            viewModel.sendEmailVerification(extractEmail())
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                VerificationViewState.NavigateToHomeScreen -> {
                    findNavController().navigate(
                        R.id.action_showEmailVerification_to_navigation_home
                    )
                }

                is VerificationViewState.ShowError -> {
                    showToast(state.message)
                }

                is VerificationViewState.ButtonState -> {
                    binding.btnSendEmail.isEnabled = state.isEnabled
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()

        viewModel.onViewResumed(extractEmail(), extractToken())

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun extractToken(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(NavController.KEY_DEEP_LINK_INTENT, Intent::class.java)
        } else {
            arguments?.getParcelable(NavController.KEY_DEEP_LINK_INTENT)
        }?.data?.getQueryParameter(TOKEN_PARAM)
    }

    private fun extractEmail(): String? {
        return arguments?.getString("email")
    }

    private fun showToast(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val TOKEN_PARAM = "token"
    }
}
