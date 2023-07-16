package com.gathering.android.navhost.di

import android.content.Context
import android.content.SharedPreferences
import com.gathering.android.common.HeaderInterceptor
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.KeyValueStorage
import com.gathering.android.common.PicassoImageLoader
import com.gathering.android.common.TokenRepo
import com.gathering.android.common.UserRepo
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideImageLoader(baseUrl: String): ImageLoader {
        return PicassoImageLoader(baseUrl)
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

    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("preference_key", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideKeyValueStorage(
        sharedPreferences: SharedPreferences
    ): KeyValueStorage {
        return KeyValueStorage(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideTokenRepo(
        keyValueStorage: KeyValueStorage
    ): TokenRepo {
        return TokenRepo(keyValueStorage)
    }

    @Provides
    @Singleton
    fun provideUserRepo(
        keyValueStorage: KeyValueStorage,
        gson: Gson
    ): UserRepo {
        return UserRepo(keyValueStorage, gson)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    companion object {
        // Better to moved to build config
        private const val SERVER_ADDRESS = "https://moyar.dev"
        private const val SERVER_PORT = "8080"
        private const val BASE_URL = "$SERVER_ADDRESS:$SERVER_PORT"
    }
}