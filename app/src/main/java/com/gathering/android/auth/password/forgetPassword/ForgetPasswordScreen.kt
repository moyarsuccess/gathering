package com.gathering.android.auth.password.forgetPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.composables.CustomActionButton
import com.gathering.android.common.composables.CustomTextView
import com.gathering.android.common.composables.EmailTextField
import com.gathering.android.common.composables.ErrorTextView
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenForgetPasswordEmailInputBinding
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForgetPasswordScreen : FullScreenBottomSheet(), ForgetPasswordNavigator {

    private lateinit var binding: ScreenForgetPasswordEmailInputBinding

    @Inject
    lateinit var viewModel: ForgetPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenForgetPasswordEmailInputBinding.inflate(layoutInflater)
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
                            ForgetPasswordScreenCompose(
                                state.value.isInProgress,
                                state.value.errorMessage,
                                viewModel::onSendLinkBtnClicked
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isComposeEnabled) {
            viewModel.onViewCreated(this)
            return
        } else {
            lifecycleScope.launch {
                viewModel.uiState.collectLatest { state ->
                    if (state.isInProgress) {
                        binding.btnSendLink.startAnimation()
                    } else {
                        binding.btnSendLink.revertAnimation()
                    }
                    state.errorMessage?.let {
                        showErrorText(it)
                    }

                }
            }
            binding.btnSendLink.setOnClickListener {
                val email = binding.etEmailInput.text.toString()
                viewModel.onSendLinkBtnClicked(email)
            }
            viewModel.onViewCreated(this)
        }
    }

    override fun navigateToResetPassInfoBottomSheet() {
        findNavController().navigate(
            R.id.action_forgetPasswordScreen_to_resetPasswordInfo
        )
    }

    @Composable
    @Preview(showBackground = true)
    fun ForgetPasswordScreenPreview() {
        ForgetPasswordScreenCompose(isInProgress = true, error = "error") {}
    }

    @Composable
    fun ForgetPasswordScreenCompose(
        isInProgress: Boolean,
        error: String? = null,
        onSendLinkBtnClicked: (email: String) -> Unit
    ) {
        var email by rememberSaveable { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextView(
                textResId = R.string.reset_pass_msg,
                modifier = Modifier
                    .fillMaxWidth(),
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black
                )
            )
            EmailTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )
            CustomActionButton(
                text = "SEND LINK",
                onClick = { onSendLinkBtnClicked(email) },
                isLoading = isInProgress,
                modifier = Modifier
                    .height(60.dp)
                    .width(170.dp),
            )
            if (error != null) {
                ErrorTextView(error)
            }
        }
    }
}