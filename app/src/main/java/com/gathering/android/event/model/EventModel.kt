package com.gathering.android.event.model

import com.google.gson.annotations.SerializedName

data class EventModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("eventName") var eventName: String? = null,
    @SerializedName("eventHostEmail") var eventHostEmail: String? = null,
    @SerializedName("eventDescription") var eventDescription: String? = null,
    @SerializedName("photoName") var photoName: String? = null,
    @SerializedName("latitude") var latitude: Double? = null,
    @SerializedName("longitude") var longitude: Double? = null,
    @SerializedName("dateTime") var dateTime: Long? = null,
    @SerializedName("attendees") var attendees: ArrayList<Attendees> = arrayListOf(),
) {
    val isMyEvent: Boolean = false
}