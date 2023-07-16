package com.gathering.android.common

import javax.inject.Inject

class TokenRepo @Inject constructor(
    private val keyValueStorage: KeyValueStorage
) {

    fun saveToken(token: String?) {
        keyValueStorage.saveData(KEY_TOKEN, token ?: "")
    }

    fun getToken(): String? {
        return keyValueStorage.getData(KEY_TOKEN)
    }

    fun isTokenValid(): Boolean {
        return getToken() != null
    }

    fun clearToken() {
        keyValueStorage.clearData(KEY_TOKEN)
    }

    companion object {
        private const val KEY_TOKEN = "token"
    }
}