package com.gathering.android.di

import android.content.Context
import com.gathering.android.event.eventdetail.acceptrepo.AcceptTypeRemoteService
import com.gathering.android.event.eventdetail.acceptrepo.ApiAttendanceStateRepository
import com.gathering.android.event.eventdetail.acceptrepo.AttendanceStateRepository
import com.gathering.android.event.repo.ApiEventRepository
import com.gathering.android.event.repo.EventRemoteService
import com.gathering.android.event.repo.EventRepository
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
        @ApplicationContext context: Context,
    ): EventRepository {
        return ApiEventRepository(eventRemoteService, context)
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
    fun provideAcceptTypeRemoteService(
        @AuthorizedRetrofitQualifier retrofit: Retrofit
    ): AcceptTypeRemoteService {
        return retrofit.create(AcceptTypeRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideAcceptTypeRepository(
        acceptTypeRemoteService: AcceptTypeRemoteService
    ): AttendanceStateRepository {
        return ApiAttendanceStateRepository(acceptTypeRemoteService)
    }
}