package com.gathering.android.utils.location.places

interface PlaceApiWrapper {

    suspend fun suggestAddressList(address: String): List<String>
}