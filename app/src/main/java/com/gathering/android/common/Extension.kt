package com.gathering.android.common

import com.gathering.android.auth.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser?.toUser(): User {
    return User(
        id = this?.uid ?: "",
        displayName = this?.displayName ?: "",
        imageUrl = this?.photoUrl.toString(),
        email = this?.email.toString(),
    )
}