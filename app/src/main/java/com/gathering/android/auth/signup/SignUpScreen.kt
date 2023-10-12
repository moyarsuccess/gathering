package com.gathering.android.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.gathering.android.common.CustomButton
import com.gathering.android.common.ErrorText
import com.gathering.android.common.GatheringEmailTextField
import com.gathering.android.common.GatheringPasswordTextField
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.common.showErrorText
import com.gathering.android.databinding.ScreenSignUpBinding
import com.gathering.android.ui.theme.GatheringTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignUpScreen : DialogFragment(), SignUpNavigator {

    private lateinit var binding: ScreenSignUpBinding

    @Inject
    lateinit var viewModel: SignUpViewModel

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
            binding = ScreenSignUpBinding.inflate(layoutInflater)
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
                            LogUpScreen(state.value.isInProgress, state.value.errorMessage)
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
            binding.btnSignUp.setOnClickListener {
                val email = binding.etMail.text.toString()
                val password = binding.etPass.text.toString()
                val confirmPassword = binding.etVerifyPass.text.toString()
                viewModel.onSignUpButtonClicked(email, password, confirmPassword)
            }

            lifecycleScope.launch {
                viewModel.uiState.collectLatest { state ->
                    if (state.isInProgress) {
                        binding.btnSignUp.startAnimation()
                    } else {
                        binding.btnSignUp.revertAnimation()
                    }
                    state.errorMessage?.let {
                        showErrorText(it)
                    }
                }
            }
            viewModel.onViewCreated(this)
        }

    }

    override fun navigateToVerification() {
        val bundle = Bundle()
        findNavController().navigate(
            R.id.action_signUpScreen_to_verificationScreen, bundle
        )
    }

    @Preview(showBackground = true, device = "spec:parent=pixel_7")
    @Composable
    fun SignUpScreenPreview() {
        LogUpScreen(false, "")
    }

    @Composable
    private fun LogUpScreen(isInProgress: Boolean, error: String? = null) {
        var email by rememberSaveable { mutableStateOf("") }
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

                GatheringEmailTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "email"
                )

                GatheringPasswordTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "password"
                )

                GatheringPasswordTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "confirm Password"
                )

                CustomButton(
                    text = "Sign Up",
                    onClick = { viewModel.onSignUpButtonClicked(email, password, confirmPassword) },
                    isLoading = isInProgress,
                )

                if (error != null) {
                    ErrorText(error)
                }
            }
    }
}