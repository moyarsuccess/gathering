package com.gathering.android.di

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
    fun providePasswordRemoteService(
        @UnauthorizedRetrofitQualifier retrofit: Retrofit
    ): PasswordRemoteService {
        return retrofit
            .create(PasswordRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun providePasswordRepository(
        passwordRemoteService: PasswordRemoteService,
        tokenRepo: TokenRepo,
        userRepo: UserRepo
    ): PasswordRepository {
        return ApiPasswordRepository(
            passwordRemoteService = passwordRemoteService,
            tokenRepo = tokenRepo,
            userRepo = userRepo,
        )
    }

    @Provides
    @Singleton
    fun provideSignInRemoteService(
        @UnauthorizedRetrofitQualifier retrofit: Retrofit
    ): SignInRemoteService {
        return retrofit.create(SignInRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideSignInRepository(
        signInRemoteService: SignInRemoteService,
        tokenRepo: TokenRepo,
        userRepo: UserRepo
    ): SignInRepository {
        return ApiSignInRepository(
            signInRemoteService = signInRemoteService,
            tokenRepo = tokenRepo,
            userRepo = userRepo,
        )
    }

    @Provides
    @Singleton
    fun provideSignUpRemoteService(
        @UnauthorizedRetrofitQualifier retrofit: Retrofit
    ): SignUpRemoteService {
        return retrofit.create(SignUpRemoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideSignUpRepository(
        signUpRemoteService: SignUpRemoteService,
    ): SignUpRepository {
        return ApiSignUpRepository(
            signUpRemoteService = signUpRemoteService
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


    @Provides
    @Singleton
    fun provideVerificationRemoteService(
        @UnauthorizedRetrofitQualifier retrofit: Retrofit
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