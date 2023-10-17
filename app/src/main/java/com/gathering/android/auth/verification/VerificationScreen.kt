package com.gathering.android.auth.verification

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.CustomActionButton
import com.gathering.android.common.CustomTextView
import com.gathering.android.common.ErrorText
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenVerificationBinding
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VerificationScreen : FullScreenBottomSheet(), VerificationNavigator {

    private lateinit var binding: ScreenVerificationBinding

    @Inject
    lateinit var viewModel: VerificationViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenVerificationBinding.inflate(layoutInflater)
            return binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val state = viewModel.uiState.collectAsState()
                            VerificationScreenCompose(state.value.isInProgress, state.value.message)
                        }

                    }
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isComposeEnabled) {
            viewModel.onViewCreated(this)
            return
        } else {
            binding.btnSendEmail.setOnClickListener {
                viewModel.onSendEmailVerificationClicked(extractEmail())
            }

            lifecycleScope.launch {
                viewModel.uiState.collectLatest { state ->
                    if (state.isInProgress) {
                        binding.btnSendEmail.startAnimation()
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            binding.btnSendEmail.revertAnimation()
                        }, 5000)
                    } else {
                        binding.btnSendEmail.revertAnimation()
                    }
                    state.message?.let {
                        showErrorText(it)
                    }
                }
            }
            viewModel.onViewCreated(this)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onViewResumed(extractEmail(), extractToken())
    }

    private fun extractToken(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(NavController.KEY_DEEP_LINK_INTENT, Intent::class.java)
        } else {
            arguments?.getParcelable(NavController.KEY_DEEP_LINK_INTENT)
        }?.data?.getQueryParameter(TOKEN_PARAM)
    }

    private fun extractEmail(): String? {
        return arguments?.getString(EMAIL_PARAM)
    }

    override fun navigateToHomeScreen() {
        findNavController().navigate(
            R.id.action_verification_to_navigation_home
        )
    }

    companion object {
        const val TOKEN_PARAM = "token"
        const val EMAIL_PARAM = "email"
    }

    @Composable
    @Preview(showBackground = true)
    fun VerificationScreenPreview() {
        VerificationScreenCompose(false, "failed to send email.")
    }

    @Composable
    fun VerificationScreenCompose(isInProgress: Boolean, error: String? = null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CustomTextView(
                textResId = R.string.verification_email_sent_please_verify_your_email,
                modifier = Modifier
                    .fillMaxWidth(),
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            )

            CustomActionButton(
                text = "Send Email",
                onClick = { viewModel.onSendEmailVerificationClicked(extractEmail()) },
                isLoading = isInProgress
            )

            if (error != null) {
                ErrorText(error)
            }
        }
    }
}