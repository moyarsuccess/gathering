package com.gathering.android.auth.model

import java.io.Serializable

data class User(
    val uId: String? = "",
    val displayName: String? = "",
    val phoneNumber: String? = "",
    val photoUrl: String? = "",
    val isEmailVerified: Boolean? = false
) : Serializable

