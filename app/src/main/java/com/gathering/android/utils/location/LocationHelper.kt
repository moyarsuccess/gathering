package com.gathering.android.utils.location

import com.gathering.android.utils.location.fused.FusedLocationWrapper
import com.gathering.android.utils.location.geocoder.GeocoderWrapper
import com.gathering.android.utils.location.places.PlaceApiWrapper

interface LocationHelper : FusedLocationWrapper, GeocoderWrapper, PlaceApiWrapper