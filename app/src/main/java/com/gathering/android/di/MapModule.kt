package com.gathering.android.di

import android.content.Context
import android.location.Geocoder
import com.gathering.android.BuildConfig
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
class MapModule {

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
    @MapApiKeyQualifier
    fun provideMapsApiKey(): String {
        return BuildConfig.MAPS_API_KEY
    }

    @Provides
    fun provideGeocoder(@ApplicationContext context: Context, locale: Locale): Geocoder {
        return Geocoder(context, locale)
    }
}