package com.gathering.android.auth.password.newPassword

import android.content.Intent
import android.os.Build
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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.auth.verification.VerificationScreen
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.composables.CustomActionButton
import com.gathering.android.common.composables.CustomTextView
import com.gathering.android.common.composables.AlertTextView
import com.gathering.android.common.composables.PasswordTextField
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenNewPasswordInputBinding
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InputNewPasswordScreen : FullScreenBottomSheet(), InputNewPasswordNavigator {

    private lateinit var binding: ScreenNewPasswordInputBinding

    @Inject
    lateinit var viewModel: InputNewPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (!isComposeEnabled) {
            binding = ScreenNewPasswordInputBinding.inflate(layoutInflater)
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
                            InputNewPasswordScreenByCompose(
                                state.value.isInProgress,
                                state.value.errorMessage,
                                viewModel::onSubmitBtnClicked
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
            binding.btnSubmit.setOnClickListener {
                val password = binding.etPassInput.text.toString()
                val confirmPassword = binding.etConfirmPassInput.text.toString()
                viewModel.onSubmitBtnClicked(extractToken(), password, confirmPassword)
            }

            lifecycleScope.launch {
                viewModel.uiState.collectLatest { state ->
                    if (state.isInProgress) {
                        binding.btnSubmit.startAnimation()
                    } else {
                        binding.btnSubmit.revertAnimation()
                    }
                    state.errorMessage?.let {
                        showErrorText(it)
                    }
                }
            }
            viewModel.onViewCreated(this)
        }
    }

    private fun extractToken(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(NavController.KEY_DEEP_LINK_INTENT, Intent::class.java)
        } else {
            arguments?.getParcelable(NavController.KEY_DEEP_LINK_INTENT)
        }?.data?.getQueryParameter(VerificationScreen.TOKEN_PARAM)
    }

    override fun navigateToHomeFragment() {
        findNavController().navigate(
            R.id.action_newPasswordInputScreen_to_navigation_IntroScreen
        )
    }

    @Composable
    @Preview(showBackground = true, device = "id:pixel_7")
    fun InputNewPasswordScreenByComposePreview() {
        InputNewPasswordScreenByCompose(
            isInProgress = false, error = "error message shown"
        ) { _, _, _ -> run {} }
    }

    @Composable
    fun InputNewPasswordScreenByCompose(
        isInProgress: Boolean,
        error: String? = null,
        onSubmitBtnClicked: (token: String?, password: String, confirmedPassword: String) -> Unit

    ) {
        var password by rememberSaveable { mutableStateOf("") }
        var confirmPassword by rememberSaveable { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextView(
                textResId = R.string.enter_new_password,
                modifier = Modifier
                    .fillMaxWidth(),
                textStyle = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            )

            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "password"
            )

            PasswordTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "confirm Password"
            )

            CustomActionButton(
                text = "SUBMIT",
                onClick = {
                    val token = extractToken()
                    onSubmitBtnClicked(token, password, confirmPassword)
                },
                isLoading = isInProgress,
                modifier = Modifier
                    .height(60.dp)
                    .width(170.dp),
            )

            if (error != null) {
                AlertTextView(error)
            }
        }
    }

}