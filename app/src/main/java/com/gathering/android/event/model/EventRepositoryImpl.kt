package com.gathering.android.event.model

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
) : EventRepository {

    override fun addEvent(event: Event, onEventRequestReady: (eventRequest: EventRequest) -> Unit) {
        fireStore.collection(EVENT_COLLECTION_NAME)
            .add(event.toEventEntity())
            .addOnSuccessListener {
                onEventRequestReady(EventRequest.Success(true))
            }
            .addOnFailureListener {
                onEventRequestReady(EventRequest.Failure(it))
            }
    }

    override fun getAllEvents(onEventRequestReady: (eventRequest: EventRequest) -> Unit) {
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
                onEventRequestReady(EventRequest.Success(eventList.toEvents()))
            }
            .addOnFailureListener { exception ->
                onEventRequestReady(EventRequest.Failure(exception))
            }
    }

    private fun Event.toEventEntity(): EventEntity {
        return EventEntity(
            eventName = this.eventName,
            hostName = this.hostName,
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
                hostName = it.hostName,
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

    companion object {
        private const val EVENT_COLLECTION_NAME = "Events"
    }
}