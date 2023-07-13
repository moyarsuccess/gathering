package com.gathering.android.navhost.di

import com.gathering.android.profile.repo.ApiProfileRepository
import com.gathering.android.profile.repo.ProfileRemoteService
import com.gathering.android.profile.repo.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProfileModule {

    @Provides
    @Singleton
    fun provideProfileRemoteService(
        @AuthorizedRetrofit retrofit: Retrofit
    ): ProfileRemoteService {
        return retrofit.create(ProfileRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileRemoteService: ProfileRemoteService
    ): ProfileRepository {
        return ApiProfileRepository(profileRemoteService)
    }
}