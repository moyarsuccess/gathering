package com.gathering.android.event.myevent

import com.gathering.android.MainDispatcherRule
import com.gathering.android.event.Event
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.repo.EventRepository
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MyEventViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var sut: MyEventViewModel

    @MockK(relaxed = true)
    private lateinit var eventRepository: EventRepository

    @MockK(relaxed = true)
    private lateinit var myEventNavigator: MyEventNavigator

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        sut = MyEventViewModel(
            eventRepository = eventRepository
        )
    }

    @Test
    fun `onViewCreated sets navigator properly`() {
        // WHEN
        sut.onViewCreated(myEventNavigator)

        // THEN
        Assert.assertEquals(myEventNavigator, sut.myEventNavigator)
    }

    @Test
    fun onEventAdded() {
    }
    @Test
    fun onNextPageRequested() {
    }

    @Test
    fun onSwipedToDelete() {
    }

    @Test
    fun onUndoClicked() {
    }

    @Test
    fun onSnackBarDismissed() {
    }

    @Test
    fun `onEditEventClicked should navigate to edit event screen`() {
        //GIVEN
        sut.onViewCreated(myEventNavigator)

        //WHEN
        sut.onEditEventClicked(event = createMockEvent())

        //THEN
        verify(exactly = 1) { sut.myEventNavigator?.navigateToEditEvent(createMockEvent()) }
    }

    @Test
    fun `onEventItemClicked should navigate to event detail screen`() {
        //GIVEN
        sut.onViewCreated(myEventNavigator)

        //WHEN
        sut.onEventItemClicked(1)

        //THEN
        verify(exactly = 1) { sut.myEventNavigator?.navigateToConfirmedAttendeesScreen(1) }
    }

    @Test
    fun `onFabButtonClicked navigates to add event screen properly`() = runTest {
        // GIVEN
        sut.onViewCreated(myEventNavigator)

        // WHEN
        sut.onFabButtonClicked()

        // THEN
        verify(exactly = 1) { sut.myEventNavigator?.navigateToAddEvent() }
    }

    private fun createMockEvent(): Event {
        return Event(
            eventId = 2L,
            dateAndTime = 2L,
            description = "",
            eventName = "",
            isMyEvent = true,
            isContactEvent = false,
            photoUrl = "",
            attendeeModels = listOf(),
            eventHostEmail = "",
            latitude = 2.11,
            longitude = 2.44,
            liked = true
        )
    }

    private fun createMockEventModel(): EventModel {
        return EventModel(
            eventName = "",
            eventHostEmail = "",
            latitude = 2.11,
            longitude = 2.44,
            liked = true,
            photoName = "",
            eventDescription = "",
            id = 2L,
            dateTime = 1L,
            attendeeModels = arrayListOf()
        )
    }

}