package com.gathering.android.auth.common

import com.gathering.android.auth.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser?.toUser(): User {
    return User(
        uId = this?.uid,
        displayName = this?.displayName,
        phoneNumber = this?.phoneNumber,
        photoUrl = this?.photoUrl.toString(),
        isEmailVerified = this?.isEmailVerified,
    )
}