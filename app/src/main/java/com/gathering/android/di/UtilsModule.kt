package com.gathering.android.di

import android.content.Context
import android.location.Geocoder
import com.gathering.android.BuildConfig
import com.gathering.android.utils.location.LocationHelper
import com.gathering.android.utils.location.LocationHelperImpl
import com.gathering.android.utils.location.fused.FusedLocationWrapper
import com.gathering.android.utils.location.fused.FusedLocationWrapperImpl
import com.gathering.android.utils.location.geocoder.GeocoderWrapper
import com.gathering.android.utils.location.geocoder.GeocoderWrapperImpl
import com.gathering.android.utils.location.places.PlaceApiWrapper
import com.gathering.android.utils.location.places.PlaceApiWrapperImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UtilsModule {

    @Provides
    @Singleton
    fun providePlacesClient(
        @ApplicationContext context: Context,
        @MapApiKeyQualifier mapApiKey: String
    ): PlacesClient {
        Places.initialize(context, mapApiKey)
        return Places.createClient(context)
    }

    @Provides
    @Singleton
    @MapApiKeyQualifier
    fun provideMapsApiKey(): String {
        return BuildConfig.MAPS_API_KEY
    }

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context, locale: Locale): Geocoder {
        return Geocoder(context, locale)
    }

    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun providePlaceApiWrapper(placesClient: PlacesClient): PlaceApiWrapper =
        PlaceApiWrapperImpl(placesClient)

    @Provides
    fun provideFusedLocationWrapper(fusedLocationProviderClient: FusedLocationProviderClient): FusedLocationWrapper {
        return FusedLocationWrapperImpl(fusedLocationProviderClient)
    }

    @Provides
    @Singleton
    fun provideGeocoderWrapper(geocoder: Geocoder): GeocoderWrapper {
        return GeocoderWrapperImpl(geocoder)
    }

    @Provides
    fun provideLocationHelper(
        geocoderWrapper: GeocoderWrapper,
        placeApiWrapper: PlaceApiWrapper,
        fusedLocationWrapper: FusedLocationWrapper
    ): LocationHelper {
        return LocationHelperImpl(
            geocoderWrapper = geocoderWrapper,
            placeApiWrapper = placeApiWrapper,
            fusedLocationWrapper = fusedLocationWrapper
        )
    }
}