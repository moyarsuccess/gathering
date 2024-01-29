package com.gathering.android.common

import javax.inject.Inject

class TokenRepository @Inject constructor(
    private val keyValueStorage: KeyValueStorage
) {

    fun saveToken(token: String?) {
        keyValueStorage.save(KEY_TOKEN, token ?: "")
    }

    fun getToken(): String {
        return keyValueStorage.getString(KEY_TOKEN)
    }

    fun isTokenValid(): Boolean {
        return getToken().isNotEmpty()
    }

    fun clearToken() {
        keyValueStorage.remove(KEY_TOKEN)
    }

    companion object {
        private const val KEY_TOKEN = "token"
    }
}