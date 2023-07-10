package com.gathering.android.common

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import javax.inject.Inject


class TokenManager @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("preference_key", MODE_PRIVATE);

    fun saveToken(token: String?) {
        sharedPreferences.edit().putString("token", token ?: "").apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove("token").apply()
    }

    fun isTokenValid(token: String): Boolean {
        return sharedPreferences.contains(token)
    }
}