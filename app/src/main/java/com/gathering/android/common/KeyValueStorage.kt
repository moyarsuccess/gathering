package com.gathering.android.common

import android.content.SharedPreferences
import javax.inject.Inject

class KeyValueStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun save(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }

    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}