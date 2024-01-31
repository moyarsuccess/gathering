package com.gathering.android.home

import com.gathering.android.MainDispatcherRule
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.repo.EventRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
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

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    private lateinit var authRepository: AuthRepository

    @MockK(relaxed = true)
    private lateinit var eventRepository: EventRepository

    @MockK(relaxed = true)
    private lateinit var homeNavigator: HomeNavigator

    @MockK(relaxed = true)
    private lateinit var classToTest: HomeViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        classToTest = HomeViewModel(authRepository, eventRepository)
    }

    @Test
    fun `onViewCreated sets navigator properly`() {
        //WHEN
        classToTest.onViewCreated(homeNavigator)

        // THEN
        Assert.assertEquals(homeNavigator, classToTest.homeNavigator)
    }

    @Test
    fun `onViewCreated navigate to Intro screen when user is not verified`() {
        //GIVEN
        every { authRepository.isUserVerified() } returns false

        //WHEN
        classToTest.onViewCreated(homeNavigator)

        //THEN
        verify(exactly = 1) { classToTest.homeNavigator?.navigateToIntroScreen() }
    }

    @Test
    fun `onViewCreated should call get event when user is verified`() {
        //GIVEN
        every { authRepository.isUserVerified() } returns true

        //WHEN
        classToTest.onViewCreated(homeNavigator)

        //THEN
        coVerify(exactly = 1) { eventRepository.getEvents(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onViewCreated should set showNoData,showProgress and event state appropriately when user is verified`() =
        runTest {
            //GIVEN
            every { authRepository.isUserVerified() } returns true
            coEvery { eventRepository.getEvents(any()) } returns listOf(
                EventModel(
                    id = 1,
                    eventName = "anahid's party",
                    eventHostEmail = "animansoubi@gmail.com",
                    eventDescription = "this is my party",
                    photoName = "",
                    latitude = 1.0,
                    longitude = 1.0,
                    dateTime = null,
                    attendeeModels = arrayListOf(),
                    liked = true
                )
            )
            val results = mutableListOf<HomeViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onViewCreated(homeNavigator)
            runCurrent()

            //THEN
            coVerify(exactly = 1) { eventRepository.getEvents(page = 1) }
            Assert.assertTrue(results[1].showProgress)
            Assert.assertFalse(results[2].showProgress)
            Assert.assertFalse(results[1].showNoData)
            Assert.assertEquals(1, results[2].events.size)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onNextPageRequested should set showNoData,showProgress and event state appropriately`() =
        runTest {
            //GIVEN
            coEvery { eventRepository.getEvents(2) } returns listOf(
                EventModel(
                    id = 1,
                    eventName = "anahid's party",
                    eventHostEmail = "animansoubi@gmail.com",
                    eventDescription = "this is my party",
                    photoName = "",
                    latitude = 1.0,
                    longitude = 1.0,
                    dateTime = null,
                    attendeeModels = arrayListOf(),
                    liked = true
                )
            )
            val results = mutableListOf<HomeViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onNextPageRequested()
            runCurrent()

            //THEN
            coVerify(exactly = 1) { eventRepository.getEvents(page = 2) }
            Assert.assertTrue(results[1].showProgress)
            Assert.assertFalse(results[2].showProgress)
            Assert.assertFalse(results[1].showNoData)
            Assert.assertEquals(1, results[2].events.size)
            job.cancel()
        }

    @Test
    fun `onEventItemClicked should navigate to event detail screen`() {
        //GIVEN
        classToTest.onViewCreated(homeNavigator)

        //WHEN
        classToTest.onEventItemClicked(1)

        //THEN
        verify(exactly = 1) { classToTest.homeNavigator?.navigateToEventDetail(1) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onEventLikeClicked should call like event`() =
        runTest {
            // GIVEN
            coEvery { authRepository.isUserVerified() } returns true
            coEvery { eventRepository.getEvents(any()) } returns listOf(
                EventModel(
                    id = 1,
                    eventName = "anahid's party",
                    eventHostEmail = "animansoubi@gmail.com",
                    eventDescription = "this is my party",
                    photoName = "",
                    latitude = 1.0,
                    longitude = 1.0,
                    dateTime = null,
                    attendeeModels = arrayListOf(),
                    liked = true
                )
            )
            val results = mutableListOf<HomeViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onViewCreated(homeNavigator)
            classToTest.onEventLikeClicked(
                mockk {
                    every { eventId } returns 1
                    every { liked } returns true
                }
            )
            runCurrent()

            //THEN
            coVerify(exactly = 1) { eventRepository.likeEvent(1, false) }
            Assert.assertTrue(results[1].showProgress)
            Assert.assertFalse(results[2].showProgress)
            Assert.assertEquals(1, classToTest.uiState.value.events[0].eventId)
            Assert.assertEquals(false, classToTest.uiState.value.events[0].liked)
            job.cancel()
        }
}