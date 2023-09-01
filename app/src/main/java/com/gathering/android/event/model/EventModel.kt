package com.gathering.android.event.model

import com.google.gson.annotations.SerializedName

data class EventModel(
    @SerializedName("id") var id: Long,
    @SerializedName("eventName") var eventName: String?,
    @SerializedName("eventHostEmail") var eventHostEmail: String?,
    @SerializedName("eventDescription") var eventDescription: String?,
    @SerializedName("photoName") var photoName: String?,
    @SerializedName("latitude") var latitude: Double?,
    @SerializedName("longitude") var longitude: Double?,
    @SerializedName("dateTime") var dateTime: Long?,
    @SerializedName("attendees") var attendees: ArrayList<Attendee>,
    @SerializedName("liked") var liked: Boolean,
) {
    val isMyEvent: Boolean = false
}