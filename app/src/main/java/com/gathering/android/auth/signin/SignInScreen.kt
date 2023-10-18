package com.gathering.android.auth.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.auth.AuthButton
import com.gathering.android.auth.CustomUnderlinedButton
import com.gathering.android.common.ErrorText
import com.gathering.android.common.GatheringEmailTextField
import com.gathering.android.common.GatheringPasswordTextField
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenSignInBinding
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignInScreen : DialogFragment(), SignInNavigator {

    private lateinit var binding: ScreenSignInBinding

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
        return if (!isComposeEnabled) {
            binding = ScreenSignInBinding.inflate(layoutInflater)
            binding.root
        } else {
            ComposeView(requireContext()).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val state = viewModel.uiState.collectAsState()
                            LogInScreen(
                                state.value.isInProgress,
                                state.value.errorMessage,
                                viewModel::onForgotPassTvClicked,
                                viewModel::onSignInButtonClicked
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
            binding.btnSignIn.setOnClickListener {
                val email = binding.etMail.text.toString()
                val pass = binding.etPass.text.toString()
                viewModel.onSignInButtonClicked(email, pass)
            }

            binding.tvForgetPassword.setOnClickListener {
                viewModel.onForgotPassTvClicked()
            }


            lifecycleScope.launch {
                viewModel.uiState.collectLatest { state ->
                    if (state.isInProgress) {
                        binding.btnSignIn.startAnimation()
                    } else {
                        binding.btnSignIn.revertAnimation()
                    }
                    state.errorMessage?.let {
                        showErrorText(it)
                    }
                }
            }
            viewModel.onViewCreated(this)
        }
    }

    override fun navigateToHome() {
        findNavController().navigate(
            R.id.action_signInScreen_to_navigation_homeScreen
        )
    }

    override fun navigateToVerification() {
        findNavController().navigate(
            R.id.action_signInScreen_to_verificationScreen
        )
    }

    override fun navigateToPasswordReset() {
        findNavController().navigate(
            R.id.action_signInScreen_to_forgetPasswordScreen
        )
    }

    @Preview(showBackground = true, device = "spec:parent=pixel_7")
    @Composable
    fun SignInScreenPreview() {
        LogInScreen(true, "user does not exist!!", {}, { _, _ -> run {} })
    }

    @Composable
    private fun LogInScreen(
        isInProgress: Boolean,
        error: String? = null,
        onForgotPasswordClicked: () -> Unit,
        onSignInButtonClicked: (email: String, password: String) -> Unit
    ) {
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GatheringEmailTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )

            GatheringPasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                CustomUnderlinedButton(text = "forgot password?",
                    onClick = { onForgotPasswordClicked() })
            }

            AuthButton(
                modifier = Modifier.padding(top = 30.dp, bottom = 30.dp),
                text = "SIGN IN",
                onClick = { onSignInButtonClicked(email, password) },
                isLoading = isInProgress
            )

            if (error != null) {
                ErrorText(error)
            }
        }
    }
}
