package com.gathering.android.common

import com.gathering.android.auth.model.User
import com.google.gson.Gson
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val keyValueStorage: KeyValueStorage,
    private val gson: Gson
) {
    fun saveUser(user: User) {
        keyValueStorage.save(USER_INFO, gson.toJson(user))
    }

    fun getUser(): User? {
        val userInfo = keyValueStorage.getString(USER_INFO)
        if (userInfo.isEmpty()) return null
        return gson.fromJson(userInfo, User::class.java)
    }

    fun clearUser() {
        keyValueStorage.remove(USER_INFO)
    }

    companion object {
        const val USER_INFO = "USER_INFO"
    }
}