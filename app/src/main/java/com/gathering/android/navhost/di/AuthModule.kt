package com.gathering.android.navhost.di

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
import com.gathering.android.common.TokenRepo
import com.gathering.android.common.UserRepo
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
    fun providePasswordRemoteService(
        @UnauthorizedRetrofit retrofit: Retrofit
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
        @UnauthorizedRetrofit retrofit: Retrofit
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
        @UnauthorizedRetrofit retrofit: Retrofit
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
        @UnauthorizedRetrofit retrofit: Retrofit
    ): VerificationRemoteService {
        return retrofit.create(VerificationRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideVerificationRepository(
        passwordRemoteService: VerificationRemoteService,
        tokenRepo: TokenRepo,
        userRepo: UserRepo
    ): VerificationRepository {
        return ApiVerificationRepository(passwordRemoteService, tokenRepo, userRepo)
    }
}