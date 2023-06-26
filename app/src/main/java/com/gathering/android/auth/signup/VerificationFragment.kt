package com.gathering.android.auth.signup

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.databinding.FrgVerificationBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VerificationFragment : DialogFragment() {

    private lateinit var binding: FrgVerificationBinding
    private var countDownTimer: CountDownTimer? = null

    @Inject
    lateinit var viewModel: VerificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen
        )
        viewModel.onSendEmailBtnClicked()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FrgVerificationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendEmail.setOnClickListener {
            viewModel.onSendEmailBtnClicked()
        }
        binding.btnVerified.setOnClickListener {
            viewModel.onVerifiedClicked()
        }
        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is VerificationViewState.NavigateToHomeScreen -> {
                    findNavController().navigate(
                        R.id.action_showEmailVerification_to_navigation_home
                    )
                }
                is VerificationViewState.NavigateToIntroPage -> {
                    findNavController().popBackStack()
                }
                is VerificationViewState.NavigateToVerification -> {
                    findNavController().popBackStack()
                }
                is VerificationViewState.Message -> {
                    showToast(state.text)
                }
                is VerificationViewState.ButtonState -> {
                    binding.btnSendEmail.isEnabled = state.isEnabled
                }
                is VerificationViewState.StartTimer -> {
                    val seconds = state.seconds
                    val onTimerFinished = state.onTimerFinished
                    startTimer(seconds, onTimerFinished)
                }
            }
        }
    }

    private fun startTimer(seconds: Int, onTimerFinished: () -> Unit) {
        countDownTimer = object : CountDownTimer(seconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.tvCountDown.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                onTimerFinished()
            }
        }.start()
        countDownTimer?.start()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    private fun showToast(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }
}
