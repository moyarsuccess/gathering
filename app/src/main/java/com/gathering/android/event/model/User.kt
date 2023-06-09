package com.gathering.android.event.model

import java.io.Serializable

data class User(
    var id: String? = "",
    var userName: String? = "",
    var email: String? = "",
    val phone: String? = ""
) : Serializable {
    override fun toString(): String {
        return userName ?: email ?: "UserId: $id"
    }
}
