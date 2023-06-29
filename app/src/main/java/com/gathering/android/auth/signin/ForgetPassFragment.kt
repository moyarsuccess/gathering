package com.gathering.android.auth.signin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.databinding.FrgPasswordResetBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForgetPassFragment : DialogFragment() {

    private lateinit var binding: FrgPasswordResetBinding

    @Inject
    lateinit var viewModel: ForgetPasswordViewModel

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
        binding = FrgPasswordResetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendLink.setOnClickListener {
            val email = binding.etEmailInput.text.toString()
            viewModel.onSendLinkBtnClicked(email)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ForgetPasswordViewState.Message -> {
                    showToast(state.text)
                }
                ForgetPasswordViewState.NavigateToResetPassInfoBottomSheet -> {
                    findNavController().navigate(
                        R.id.action_forgetPasswordFragment_to_resetPassInfoBottomSheet
                    )
                }
            }
        }

    }
    private fun showToast(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }
}