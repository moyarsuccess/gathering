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
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerFinished = false


    @Inject
    lateinit var viewModel: VerificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen
        )
        startTimer()
        viewModel.onSendEmailVerification()
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
            //send email verification and start countdown again
            if (isTimerFinished) {
                //sendEmailVerification()
            } else {
                showToast("please wait for the timer to finish")
            }

            startTimer()

            binding.btnBackToSignIn.setOnClickListener {
                navigateToSignInPage()
            }
        }
        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is VerificationViewState.NavigateToHomeScreen -> {
                    findNavController().popBackStack()
                }
                is VerificationViewState.NavigateToIntroPage -> {
                    findNavController().popBackStack()
                }
                is VerificationViewState.NavigateToVerification -> {
                    findNavController().popBackStack()
                }
                is VerificationViewState.SendEmailAgainVisibility -> {
                    binding.btnSendEmail.isEnabled = state.isSendEmailAgainVisibility
                }
                VerificationViewState.NavigateToSignIn -> {
                    findNavController().popBackStack()
                    R.id.action_showEmailVerification_to_signInFragment
                }
                is VerificationViewState.Message -> {
                    showToast(state.text)
                }
            }
        }
    }

    private fun navigateToSignInPage() {
        findNavController().navigate(R.id.action_showEmailVerification_to_signInFragment)
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.tvCountDown.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                isTimerFinished = true
            }
        }.start()

        countDownTimer.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

    private fun showToast(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }
}
