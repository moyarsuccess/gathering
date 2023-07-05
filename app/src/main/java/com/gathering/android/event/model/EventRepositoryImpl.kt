package com.gathering.android.event.model

import com.gathering.android.common.ResponseState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : EventRepository {

    override fun addEvent(event: Event, onEventRequestReady: (eventRequest: ResponseState) -> Unit) {
        fireStore.collection(EVENT_COLLECTION_NAME)
            .add(event.toEventEntity())
            .addOnSuccessListener {
                onEventRequestReady(ResponseState.Success(true))
            }
            .addOnFailureListener {
                onEventRequestReady(ResponseState.Failure(it))
            }
    }

    override fun getAllEvents(onEventRequestReady: (eventRequest: ResponseState) -> Unit) {
        fireStore.collection(EVENT_COLLECTION_NAME).get()
            .addOnSuccessListener { documentSnapshots ->
                val eventList = mutableListOf<EventEntity>()
                if (!documentSnapshots.isEmpty) {
                    for (snapshot in documentSnapshots) {
                        eventList.add(
                            snapshot.toObject(
                                EventEntity::class.java
                            )
                        )
                    }
                }
                onEventRequestReady(ResponseState.Success(eventList.toEvents()))
            }
            .addOnFailureListener { exception ->
                onEventRequestReady(ResponseState.Failure(exception))
            }
    }

    override fun getMyEvents(
        onEventRequestReady: (eventRequest: ResponseState) -> Unit
    ) {

        fireStore.collection(EVENT_COLLECTION_NAME)
            .whereEqualTo("host.email", auth.currentUser?.email ?: "").get()
            .addOnSuccessListener { documentSnapshots ->
                val eventList = mutableListOf<EventEntity>()
                if (!documentSnapshots.isEmpty) {
                    for (snapshot in documentSnapshots) {
                        eventList.add(
                            snapshot.toObject(
                                EventEntity::class.java
                            )
                        )
                    }
                }
                onEventRequestReady(ResponseState.Success(eventList.toEvents()))
            }
            .addOnFailureListener { exception ->
                onEventRequestReady(ResponseState.Failure(exception))
            }
    }

    private fun Event.toEventEntity(): EventEntity {
        return EventEntity(
            eventName = this.eventName,
            host = userFromCurrentUser(),
            description = this.description,
            photoUrl = this.photoUrl,
            location = this.location,
            timeStamp = this.dateAndTime,
            isInFriendZoneEvent = this.isContactEvent,
            isMyEvent = this.isMyEvent,
            attendees = this.attendees,
        )
    }

    private fun List<EventEntity>.toEvents(): List<Event> {
        return map {
            Event(
                eventName = it.eventName,
                host = it.host,
                description = it.description,
                photoUrl = it.photoUrl,
                location = it.location,
                dateAndTime = it.timeStamp,
                isContactEvent = it.isInFriendZoneEvent,
                isMyEvent = it.isMyEvent,
                attendees = it.attendees,
            )
        }
    }

    private fun userFromCurrentUser(): User {
        val user = auth.currentUser ?: return User()
        return User(user.uid, user.displayName, user.email, user.phoneNumber)
    }

    companion object {
        private const val EVENT_COLLECTION_NAME = "Events"
    }
}