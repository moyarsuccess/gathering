package com.gathering.android.di

import com.gathering.android.auth.repo.ApiAuthRepository
import com.gathering.android.auth.repo.AuthRemoteService
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.common.TokenRepository
import com.gathering.android.common.UserRepository
import com.gathering.android.notif.FirebaseDeviceTokenChangeService
import com.gathering.android.notif.FirebaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Provides
    @Singleton
    fun provideAuthRemoteService(
        @UnauthorizedRetrofitQualifier retrofit: Retrofit
    ): AuthRemoteService {
        return retrofit
            .create(AuthRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authRemoteService: AuthRemoteService,
        tokenRepository: TokenRepository,
        userRepository: UserRepository
    ): AuthRepository {
        return ApiAuthRepository(
            remoteService = authRemoteService,
            tokenRepository = tokenRepository,
            userRepository = userRepository
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseMessagingRepository(firebaseDeviceTokenChangeService: FirebaseDeviceTokenChangeService): FirebaseRepository {
        return FirebaseRepository(firebaseDeviceTokenChangeService)
    }

    @Provides
    fun firebaseDeviceTokenChangeService(
        @AuthorizedRetrofitQualifier retrofit: Retrofit
    ): FirebaseDeviceTokenChangeService {
        return retrofit.create(FirebaseDeviceTokenChangeService::class.java)
    }
}