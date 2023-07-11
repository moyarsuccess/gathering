package com.gathering.android.auth.model

import java.io.Serializable

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val imageUrl: String = "",
) : Serializable

