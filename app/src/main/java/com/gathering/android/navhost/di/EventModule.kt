package com.gathering.android.navhost.di

import android.content.Context
import com.gathering.android.event.model.EventRepository
import com.gathering.android.event.model.EventRepositoryImpl
import com.gathering.android.event.myevent.addevent.repo.APIAddEventRepository
import com.gathering.android.event.myevent.addevent.repo.AddEventRemoteService
import com.gathering.android.event.myevent.addevent.repo.AddEventRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
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
        firebaseFirestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): EventRepository {
        return EventRepositoryImpl(firebaseFirestore, auth)
    }

    @Provides
    @Singleton
    fun provideAddEventRepository(
        @ApplicationContext context: Context,
        addEventRemoteService: AddEventRemoteService
    ): AddEventRepository {
        return APIAddEventRepository(context, addEventRemoteService)
    }

    @Provides
    @Singleton
    fun provideAddEventRemoteService(
        @AuthorizedRetrofitQualifier retrofit: Retrofit
    ): AddEventRemoteService {
        return retrofit.create(AddEventRemoteService::class.java)
    }

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }
}