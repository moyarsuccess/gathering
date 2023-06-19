package com.gathering.android.auth.signup

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.gathering.android.R
import com.gathering.android.auth.AuthRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class VerificationViewModel @javax.inject.Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<VerificationViewState>()
    val viewState: LiveData<VerificationViewState> by ::_viewState

    fun onSendEmailBtnClicked() {
        repository.verifyUser()
        // start the timer
        // send verification email
    }
    fun onSendEmailVerification() {
            val user = Firebase.auth.currentUser
            user?.sendEmailVerification()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("verificationEmail","email verification sent successfully.")
                } else {
                    _viewState.value = VerificationViewState.Message("Failed to send Email Verification, try again!")
                }
            }
        }

    fun onBackToSignInClicked(){
    }
}