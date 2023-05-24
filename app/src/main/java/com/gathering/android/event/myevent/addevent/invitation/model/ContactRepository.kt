package com.gathering.android.event.myevent.addevent.invitation.model

interface ContactRepository {
    fun getContactInfo(name: String): Contact?
    fun getContactList(query: String): List<Contact>
}