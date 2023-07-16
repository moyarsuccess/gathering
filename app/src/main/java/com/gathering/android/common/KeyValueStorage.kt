package com.gathering.android.common

import android.content.SharedPreferences
import javax.inject.Inject

class KeyValueStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun saveData(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value ?: "").apply()
    }

    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun clearData(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}