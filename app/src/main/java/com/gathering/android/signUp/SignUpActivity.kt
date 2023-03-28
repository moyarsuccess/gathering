package com.gathering.android.signUp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.gathering.android.EventActivity
import com.gathering.android.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import javax.inject.Inject


@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    @Inject
    lateinit var viewModel: SignUpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etMail.doOnTextChanged { text, _, _, _ ->
            viewModel.onEmailAddressChanged(text.toString())
        }

        binding.etPass.doOnTextChanged { text, _, _, _ ->
            viewModel.onPasswordChanged(text.toString())
        }

        binding.etVerifyPass.doOnTextChanged { text, _, _, _ ->
            val pass = binding.etPass.text.toString()
            val confirmedPass = text.toString()
            viewModel.onConfirmedPasswordChanged(pass, confirmedPass)
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.etMail.text.toString()
            val pass = binding.etPass.text.toString()
            viewModel.onSignUpButtonClicked(email, pass)
        }

        viewModel.viewState.observe(this) { state ->
            when (state) {
                is SignUpScreenViewState.Error.ShowEmptyEmailError -> {
                    binding.etMail.error = state.errorMessage
                }
                is SignUpScreenViewState.Error.ShowInvalidEmailError -> {
                    binding.etMail.error = state.errorMessage
                }
                is SignUpScreenViewState.Error.ShowEmptyPassError -> {
                    binding.etPass.error = state.errorMessage
                }
                is SignUpScreenViewState.Error.ShowInvalidPassError -> {
                    binding.etPass.error = state.errorMessage
                }
                is SignUpScreenViewState.Error.ShowEmptyConfirmedPassError -> {
                    binding.etVerifyPass.error = state.errorMessage
                }
                is SignUpScreenViewState.Error.ShowInvalidConfirmedPassError -> {
                    binding.etVerifyPass.error = state.errorMessage
                }
                is SignUpScreenViewState.Error.ShowGeneralError -> {
                    showToast(state.errorMessage)
                }
                is SignUpScreenViewState.NavigateToEventScreen -> {
                    val intent = Intent(this, EventActivity::class.java)
                    intent.putExtra("extra_object", state.user as Serializable)
                    startActivity(intent)
                }
                is SignUpScreenViewState.SignUpButtonVisibility -> {
                    binding.btnSignUp.isEnabled = state.isSignUpButtonEnabled
                }
                is SignUpScreenViewState.Error.ShowAuthenticationFailedError -> {
                    showToast(state.errorMessage)
                }
            }
        }
    }

    private fun showToast(errorMessage: String?) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}