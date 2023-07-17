package com.gathering.android.auth.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    @SerializedName("imageUrl")
    val photoName: String = "",
) : Serializable
