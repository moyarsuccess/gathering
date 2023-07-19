package com.gathering.android.common.di

import android.content.Context
import com.gathering.android.event.myevent.addevent.invitation.model.ContactRepository
import com.gathering.android.event.myevent.addevent.invitation.model.ContactRepositoryImpl
import contacts.core.Contacts
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class InvitationModule {

    @Provides
    fun provideContactRepository(contacts: Contacts): ContactRepository {
        return ContactRepositoryImpl(contacts)
    }

    @Provides
    fun provideContacts(@ApplicationContext context: Context): Contacts {
        return Contacts(context)
    }
}