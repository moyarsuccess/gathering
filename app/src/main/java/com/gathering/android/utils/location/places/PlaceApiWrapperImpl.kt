package com.gathering.android.utils.location.places

import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class PlaceApiWrapperImpl(
    private val placesClient: PlacesClient
) : PlaceApiWrapper {
    override suspend fun suggestAddressList(address: String): List<String> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                val token = AutocompleteSessionToken.newInstance()
                val request = FindAutocompletePredictionsRequest
                    .builder()
                    .setSessionToken(token)
                    .setCountries(listOf("CA", "US"))
                    .setQuery(address)
                    .build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        val addresses = response.autocompletePredictions.map {
                            it.getPrimaryText(null).toString()
                        }
                        continuation.resume(addresses)
                    }
                    .addOnFailureListener {
                        continuation.resume(listOf())
                    }
            }
        }
}