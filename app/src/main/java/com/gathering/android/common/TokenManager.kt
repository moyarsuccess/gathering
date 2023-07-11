package com.gathering.android.common

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import javax.inject.Inject


private const val KEY_TOKEN = "token"

class TokenManager @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("preference_key", MODE_PRIVATE);

    fun saveToken(token: String?) {
        sharedPreferences.edit().putString(KEY_TOKEN, token ?: "").apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
    }

    fun isTokenValid(): Boolean {
        return getToken() != null
    }
}