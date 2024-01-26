package com.gathering.android.utils

import java.util.regex.Pattern
import javax.inject.Inject

class ValidationChecker @Inject constructor() {

    fun isEmailValid(email: String): Boolean {
        return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.isNotEmpty()
    }

    fun isConfirmedPassValid(pass: String, confirmedPass: String): Boolean {
        val matcher = Pattern.compile(PASSWORD_REGEX).matcher(confirmedPass)
        return (pass == confirmedPass && matcher.matches())
    }

    companion object {
        private const val PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
    }
}