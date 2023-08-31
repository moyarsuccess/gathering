package com.gathering.android.auth.password.newPassword

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.auth.verification.VerificationScreen
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.FrgNewPasswordInputBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InputNewPasswordFragment : DialogFragment() {

    private lateinit var binding: FrgNewPasswordInputBinding

    @Inject
    lateinit var viewModel: InputNewPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FrgNewPasswordInputBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setOnClickListener {
            val password = binding.etPassInput.text.toString()
            val confirmPassword = binding.etConfirmPassInput.text.toString()
            viewModel.onSubmitBtnClicked(extractToken(), password, confirmPassword)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is InputNewPasswordViewState.Message -> {
                    state.text?.let {
                        showErrorText(it)
                    }

                }
                InputNewPasswordViewState.NavigateToHomeFragment -> {
                    findNavController().navigate(
                        R.id.action_newPasswordInputFragment_to_navigation_homeFragment
                    )
                }
            }
        }

    }

    private fun extractToken(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(NavController.KEY_DEEP_LINK_INTENT, Intent::class.java)
        } else {
            arguments?.getParcelable(NavController.KEY_DEEP_LINK_INTENT)
        }?.data?.getQueryParameter(VerificationScreen.TOKEN_PARAM)
    }
}