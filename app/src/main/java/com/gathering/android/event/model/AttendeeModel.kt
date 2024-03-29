package com.gathering.android.event.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AttendeeModel(
    @SerializedName("id") var id: String? = "",
    @SerializedName("email") var email: String? = "",
    @SerializedName("displayName") var displayName: String? = "",
    @SerializedName("password") var password: String? = "",
    @SerializedName("imageName") var imageName: String? = "",
    @SerializedName("activated") var activated: Boolean? = false,
    @SerializedName("accepted") var accepted: String = ""
) : Serializable