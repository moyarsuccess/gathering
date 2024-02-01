package com.gathering.android.profile.favoriteEvent

import com.gathering.android.MainDispatcherRule
import com.gathering.android.event.Event
import com.gathering.android.event.GENERAL_ERROR
import com.gathering.android.event.SERVER_NOT_RESPONDING_TO_SHOW_MY_FAVORITE_EVENT
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.repo.EventException
import com.gathering.android.event.repo.EventRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoriteEventScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var sut: FavoriteEventScreenViewModel

    @MockK(relaxed = true)
    private lateinit var eventRepository: EventRepository

    @MockK(relaxed = true)
    private lateinit var favoriteEventNavigator: FavoriteEventNavigator

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        sut = FavoriteEventScreenViewModel(
            eventRepository = eventRepository
        )
    }

    @Test
    fun `onViewCreated sets navigator properly`() {
        // WHEN
        sut.onViewCreated(favoriteEventNavigator)

        // THEN
        Assert.assertEquals(favoriteEventNavigator, sut.favoriteEventNavigator)
    }

    @Test
    fun `onViewCreated loads favorite events`() = runTest {
        // GIVEN
        coEvery { eventRepository.getMyLikedEvents(any()) } returns listOf(createMockEventModel())

        // WHEN
        sut.onViewCreated(favoriteEventNavigator)

        // THEN
        val currentState = sut.uiState.value
        assertEquals(1, currentState.favoriteEvents.size)
        assertFalse(currentState.showNoData)
        assertFalse(currentState.showProgress)
        assertNull(currentState.errorMessage)
    }

    @Test
    fun `onViewCreated does NOT load favorite events if there is a server error`() = runTest {
        // GIVEN
        coEvery { eventRepository.getMyLikedEvents(1) } throws EventException.ServerNotRespondingException

        // WHEN
        sut.onViewCreated(favoriteEventNavigator)

        // THEN
        Assert.assertEquals(
            SERVER_NOT_RESPONDING_TO_SHOW_MY_FAVORITE_EVENT,
            sut.uiState.value.errorMessage
        )
    }

    @Test
    fun `onNextPageRequested loads next page of favorite events`() = runTest {
        // GIVEN
        coEvery { eventRepository.getMyLikedEvents(any()) } returns listOf(createMockEventModel())

        // WHEN
        sut.onNextPageRequested()

        // THEN
        val currentState = sut.uiState.value
        assertEquals(1, currentState.favoriteEvents.size)
        assertFalse(currentState.showNoData)
        assertFalse(currentState.showProgress)
        assertNull(currentState.errorMessage)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onNextPageRequested sets showNoData, showProgress, and event state properly`() =
        runTest {
            // GIVEN
            coEvery { eventRepository.getEvents(2) } returns listOf(createMockEventModel())
            val results = mutableListOf<FavoriteEventScreenViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.uiState.toList(results)
            }

            // WHEN
            sut.onNextPageRequested()
            runCurrent()

            // THEN
            coVerify(exactly = 1) { eventRepository.getMyLikedEvents(page = 2) }
            Assert.assertFalse(results[0].showProgress)
            Assert.assertTrue(results[1].showProgress)
            Assert.assertFalse(results[0].showNoData)
            Assert.assertEquals(0, results[1].favoriteEvents.size)
            job.cancel()
        }

    @Test
    fun `onEventItemClicked navigates to event detail properly`() = runTest {
        // GIVEN
        sut.onViewCreated(favoriteEventNavigator)

        // WHEN
        sut.onEventItemClicked(createMockEvent())

        // THEN
        verify(exactly = 1) { sut.favoriteEventNavigator?.navigateToEventDetail(createMockEvent()) }
    }

    @Test
    fun `onEventItemClicked does NOT navigate to event detail properly`() = runTest {
        // GIVEN
        coEvery { eventRepository.getMyLikedEvents(1) } throws Exception(GENERAL_ERROR)

        // WHEN
        sut.onViewCreated(favoriteEventNavigator)

        // THEN
        Assert.assertEquals(
            GENERAL_ERROR,
            sut.uiState.value.errorMessage
        )
    }

    @Test
    fun `onNextPageRequested throws exception`() = runTest {
        // GIVEN
        coEvery { eventRepository.getMyLikedEvents(any()) } throws EventException.GeneralException

        // WHEN
        sut.onNextPageRequested()

        // THEN
        Assert.assertEquals(GENERAL_ERROR, sut.uiState.value.errorMessage)

    }

    private fun createMockEventModel(): EventModel {
        return EventModel(
            liked = true,
            longitude = 123.00,
            latitude = 234.00,
            eventHostEmail = "",
            eventName = "",
            attendeeModels = arrayListOf(),
            id = 23,
            eventDescription = "",
            dateTime = 2L,
            photoName = ""
        )
    }

    private fun createMockEvent(): Event {
        return Event(
            liked = true,
            longitude = 123.00,
            latitude = 234.00,
            eventHostEmail = "",
            eventName = "",
            attendeeModels = arrayListOf(),
            isMyEvent = true,
            eventId = 12L,
            photoUrl = "",
            isContactEvent = false,
            description = "",
            dateAndTime = 2L
        )
    }
}