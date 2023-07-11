package com.gathering.android.di

import android.content.Context
import com.gathering.android.Application
import com.gathering.android.auth.password.repo.ApiPasswordRepository
import com.gathering.android.auth.password.repo.PasswordRemoteService
import com.gathering.android.auth.password.repo.PasswordRepository
import com.gathering.android.auth.signin.repo.ApiSignInRepository
import com.gathering.android.auth.signin.repo.SignInRemoteService
import com.gathering.android.auth.signin.repo.SignInRepository
import com.gathering.android.auth.signup.repo.ApiSignUpRepository
import com.gathering.android.auth.signup.repo.SignUpRemoteService
import com.gathering.android.auth.signup.repo.SignUpRepository
import com.gathering.android.auth.verification.repo.ApiVerificationRepository
import com.gathering.android.auth.verification.repo.VerificationRemoteService
import com.gathering.android.auth.verification.repo.VerificationRepository
import com.gathering.android.common.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Provides
    @Singleton
    fun providePasswordRemoteService(
        retrofit: Retrofit
    ): PasswordRemoteService {
        return retrofit.create(PasswordRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun providePasswordRepository(
        passwordRemoteService: PasswordRemoteService
    ): PasswordRepository {
        return ApiPasswordRepository(passwordRemoteService)
    }

    @Provides
    @Singleton
    fun provideSignInRemoteService(
        retrofit: Retrofit
    ): SignInRemoteService {
        return retrofit.create(SignInRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideSignInRepository(
        passwordRemoteService: SignInRemoteService
    ): SignInRepository {
        return ApiSignInRepository(passwordRemoteService)
    }

    @Provides
    @Singleton
    fun provideSignUpRemoteService(
        retrofit: Retrofit
    ): SignUpRemoteService {
        return retrofit.create(SignUpRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideSignUpRepository(
        passwordRemoteService: SignUpRemoteService
    ): SignUpRepository {
        return ApiSignUpRepository(passwordRemoteService)
    }

    @Provides
    @Singleton
    fun provideVerificationRemoteService(
        retrofit: Retrofit
    ): VerificationRemoteService {
        return retrofit.create(VerificationRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideVerificationRepository(
        passwordRemoteService: VerificationRemoteService,
        tokenManager: TokenManager
    ): VerificationRepository {
        return ApiVerificationRepository(passwordRemoteService, tokenManager)
    }
}