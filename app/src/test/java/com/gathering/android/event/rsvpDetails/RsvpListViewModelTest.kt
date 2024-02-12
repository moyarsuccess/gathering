package com.gathering.android.event.rsvpDetails

import com.gathering.android.MainDispatcherRule
import com.gathering.android.event.model.AttendeeModel
import com.gathering.android.event.model.EventModel
import com.gathering.android.event.repo.EventRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RsvpListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    private lateinit var eventRepository: EventRepository

    private lateinit var classToTest: RsvpListViewModel


    @Before
    fun setup() {
        MockKAnnotations.init(this)
        classToTest = RsvpListViewModel(eventRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onViewCreated should set image url, even name, and attendeeModels`() = runTest {
        //GIVEN
        val event = EventModel(
            id = 1,
            eventName = "ani party",
            eventHostEmail = "animansoubi@gmail.com",
            eventDescription = "Join us and enjoy",
            latitude = null,
            longitude = null,
            photoName = "https:/moyar.server",
            dateTime = null,
            attendeeModels = arrayListOf(AttendeeModel(email = "animansoubi@gmail.com")),
            liked = true,
        )
        coEvery { eventRepository.getEventById(event.id) } returns event
        val results = mutableListOf<RsvpListViewModel.UiState>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            classToTest.uiState.toList(results)
        }

        //WHEN
        classToTest.onViewCreated(event.id)

        //THEN
        coVerify(exactly = 1) { eventRepository.getEventById(event.id) }
        Assert.assertEquals(event.photoName, results[2].imageUri)
        Assert.assertEquals(event.eventName, results[2].eventName)
        Assert.assertEquals(1, results[2].attendeeModels.size)
        Assert.assertFalse(results[2].showNoData)
        job.cancel()

    }
    
}