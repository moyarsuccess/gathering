package com.gathering.android.di

import android.content.Context
import com.gathering.android.event.model.repo.ApiEventRepository
import com.gathering.android.event.model.repo.EventRemoteService
import com.gathering.android.event.model.repo.EventRepository
import com.gathering.android.event.putevent.repo.ApiPutEventRepository
import com.gathering.android.event.putevent.repo.PutEventRemoteService
import com.gathering.android.event.putevent.repo.PutEventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EventModule {

    @Provides
    @Singleton
    fun provideEventRepository(
        eventRemoteService: EventRemoteService,
    ): EventRepository {
        return ApiEventRepository(eventRemoteService)
    }

    @Provides
    @Singleton
    fun provideEventRemoteService(
        @AuthorizedRetrofitQualifier retrofit: Retrofit
    ): EventRemoteService {
        return retrofit.create(EventRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideAddEventRepository(
        @ApplicationContext context: Context,
        putEventRemoteService: PutEventRemoteService
    ): PutEventRepository {
        return ApiPutEventRepository(context, putEventRemoteService)
    }

    @Provides
    @Singleton
    fun provideAddEventRemoteService(
        @AuthorizedRetrofitQualifier retrofit: Retrofit
    ): PutEventRemoteService {
        return retrofit.create(PutEventRemoteService::class.java)
    }
}