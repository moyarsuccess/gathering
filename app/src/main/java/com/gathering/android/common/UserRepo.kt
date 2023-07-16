package com.gathering.android.common

import com.gathering.android.auth.model.User
import com.google.gson.Gson
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val keyValueStorage: KeyValueStorage,
    private val gson: Gson
) {

    fun saveUser(user: User) {
        keyValueStorage.saveData(USER_INFO, gson.toJson(user))
    }

    fun getUser():User {
        val userInfo = keyValueStorage.getData(USER_INFO)
        return gson.fromJson(userInfo, User::class.java)
    }

    fun clearUser() {
        keyValueStorage.clearData(USER_INFO)
    }

    companion object {
        const val USER_INFO = "USER_INFO"
    }
}