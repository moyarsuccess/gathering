package com.gathering.android.navhost.di

import android.content.Context
import com.gathering.android.common.UserRepo
import com.gathering.android.profile.repo.ApiProfileRepository
import com.gathering.android.profile.repo.ProfileRemoteService
import com.gathering.android.profile.repo.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProfileModule {

    @Provides
    @Singleton
    fun provideProfileRemoteService(
        @AuthorizedRetrofitQualifier retrofit: Retrofit
    ): ProfileRemoteService {
        return retrofit.create(ProfileRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileRemoteService: ProfileRemoteService,
        @ApplicationContext context: Context,
        userRepo: UserRepo
    ): ProfileRepository {
        return ApiProfileRepository(
            context = context,
            profileRemoteService = profileRemoteService,
            userRepo
        )
    }
}