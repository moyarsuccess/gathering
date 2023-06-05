package com.gathering.android.di

import com.gathering.android.event.model.EventRepository
import com.gathering.android.event.model.EventRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EventModule {

    @Provides
    @Singleton
    fun provideEventRepository(firebaseFirestore: FirebaseFirestore): EventRepository {
        return EventRepositoryImpl(firebaseFirestore)
    }

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}