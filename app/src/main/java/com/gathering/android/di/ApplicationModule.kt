package com.gathering.android.di

import android.content.Context
import android.content.SharedPreferences
import com.gathering.android.common.ImageLoader
import com.gathering.android.common.KeyValueStorage
import com.gathering.android.common.PicassoImageLoader
import com.gathering.android.common.TokenRepo
import com.gathering.android.common.UserRepo
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideImageLoader(baseUrl: String, picasso: Picasso): ImageLoader {
        return PicassoImageLoader(baseUrl, picasso)
    }

    @Provides
    @Singleton
    fun providePicasso(): Picasso = Picasso.get()

    @Provides
    @Singleton
    fun provideLocale(): Locale {
        return Locale.getDefault()
    }

    @Provides
    @Singleton
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
}