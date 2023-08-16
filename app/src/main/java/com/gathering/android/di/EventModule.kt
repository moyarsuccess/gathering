package com.gathering.android.di

import android.content.Context
import com.gathering.android.common.UserRepo
import com.gathering.android.event.myevent.repo.AcceptTypeRemoteService
import com.gathering.android.event.myevent.repo.AcceptTypeRepository
import com.gathering.android.event.myevent.repo.ApiAcceptTypeRepository
import com.gathering.android.event.model.repo.ApiEventRepository
import com.gathering.android.event.model.repo.EventRemoteService
import com.gathering.android.event.model.repo.EventRepository
import com.gathering.android.event.putevent.repo.PutEventRemoteService
import com.gathering.android.event.putevent.repo.PutEventRepository
import com.gathering.android.event.putevent.repo.ApiPutEventRepository
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
    @Provides
    @Singleton
    fun provideAcceptTypeRemoteService(
        @AuthorizedRetrofitQualifier retrofit: Retrofit
    ): AcceptTypeRemoteService {
        return retrofit.create(AcceptTypeRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideAcceptTypeRepository(
        acceptTypeRemoteService: AcceptTypeRemoteService
    ): AcceptTypeRepository {
        return ApiAcceptTypeRepository(acceptTypeRemoteService)
    }
}