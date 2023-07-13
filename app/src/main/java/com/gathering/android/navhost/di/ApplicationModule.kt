package com.gathering.android.navhost.di

import com.gathering.android.common.HeaderInterceptor
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.PicassoImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideImageLoader(): ImageLoader {
        return PicassoImageLoader()
    }

    @Provides
    fun provideLocale(): Locale {
        return Locale.getDefault()
    }

    @Provides
    fun provideBaseUrl(): String {
        return BASE_URL
    }

    @Singleton
    @Provides
    @UnauthorizedRetrofit
    fun provideUnauthorizedRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @AuthorizedRetrofit
    fun provideAuthorizedRetrofit(
        baseUrl: String,
        interceptor: HeaderInterceptor
    ): Retrofit {
        val client = OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        // Better to moved to build config
        private const val SERVER_ADDRESS = "http://138.197.145.209"
        private const val SERVER_PORT = "8080"
        private const val BASE_URL = "$SERVER_ADDRESS:$SERVER_PORT"
    }
}