package com.gathering.android.navhost.di

import com.gathering.android.common.HeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideBaseUrl(): String {
        return BASE_URL
    }

    @Singleton
    @Provides
    @UnauthorizedRetrofitQualifier
    fun provideUnauthorizedRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @AuthorizedRetrofitQualifier
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
        private const val SERVER_ADDRESS = "https://moyar.dev"
        private const val SERVER_PORT = "8080"
        private const val BASE_URL = "$SERVER_ADDRESS:$SERVER_PORT"
    }
}