package com.gathering.android.common

import com.gathering.android.auth.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser?.toUser(): User {
    return User(
        userId = this?.uid,
        displayName = this?.displayName,
        phoneNumber = this?.phoneNumber,
        photoUrl = this?.photoUrl.toString(),
        email = this?.email.toString(),
        isEmailVerified = this?.isEmailVerified,
    )
}