package com.gathering.android.event.myevent.addevent.invitation.model

import contacts.core.Contacts
import contacts.core.equalTo
import contacts.core.util.phones
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contacts: Contacts
) : ContactRepository {

    override fun getContactList(query: String): List<Contact> {
        return contacts
            .broadQuery()
            .wherePartiallyMatches(query)
            .find()
            .map { it.toContact() }
    }

    override fun getContactInfo(name: String): Contact? {
        return contacts
            .query()
            .where { Name.DisplayName equalTo name }
            .limit(1)
            .find()
            .map { it.toContact() }
            .firstOrNull()
    }

    private fun contacts.core.entities.Contact.toContact(): Contact {
        return Contact(
            photoUrl = this.photoUri?.toString() ?: "",
            name = this.displayNamePrimary ?: "",
            number = this.phones().joinToString(",") { it.number ?: "" }
        )
    }
}