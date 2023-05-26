package com.gathering.android.event.myevent.addevent.invitation.model

import java.io.Serializable

data class Contact(
    val photoUrl: String? = "",
    val name: String? = "",
    val number: String? = ""
): Serializable {
    override fun toString(): String {
        return name ?: ""
    }
}
