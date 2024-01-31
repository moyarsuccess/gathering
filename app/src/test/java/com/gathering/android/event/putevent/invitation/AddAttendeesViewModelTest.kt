package com.gathering.android.event.putevent.invitation

import com.gathering.android.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddAttendeesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var sut: AddAttendeesViewModel

    @MockK(relaxed = true)
    private lateinit var addAttendeeNavigator: AddAttendeeNavigator

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        sut = AddAttendeesViewModel()
    }

    @Test
    fun `onViewCreated sets navigator properly`() {
        // GIVEN
        val mockNavigator = mockk<AddAttendeeNavigator>()

        // WHEN
        sut.onViewCreated("test", mockNavigator)

        // THEN
        Assert.assertEquals(mockNavigator, sut.addAttendeeNavigator)
    }

    @Test
    fun `onOKButtonClicked navigates to AddEvent screen with email list`() {
        // GIVEN
        val attendees = "email1,email2,email3"
        sut.onViewCreated(attendees, addAttendeeNavigator)

        // WHEN
        sut.onOKButtonClicked()

        // THEN
        verify { addAttendeeNavigator.navigateToAddEvent(any()) }
    }

    @Test
    fun `onOKButtonClicked navigates to AddEvent screen with empty email list`() {
        // GIVEN
        val attendees = ""
        sut.onViewCreated(attendees, addAttendeeNavigator)

        // WHEN
        sut.onOKButtonClicked()

        // THEN
        verify { addAttendeeNavigator.navigateToAddEvent(any()) }
    }
    @Test
    fun `onAttendeeRemoveItemClicked removes email from UI state`() {
        // GIVEN
        val attendees = "test1@example.com,test2@example.com"

        // WHEN
        sut.onAttendeeRemoveItemClicked(attendees)

        // THEN
        Assert.assertFalse(sut.uiState.value.attendeesEmailList.contains("test1@example.com"))
    }

    @Test
    fun `onAttendeeEmailChanged updates state`(){
        // GIVEN
        val email = "test@email.com"

        // WHEN
        sut.onAttendeeEmailChanged(email)

        // THEN
        assert(sut.uiState.value.attendeeEmail == email)
    }

    @Test
    fun onOKButtonClicked() {
    }

    @Test
    fun `onAttendeeEmailChanged`() {
    }

    @Test
    fun `onAddAttendeeButtonClicked sets uiState error message when email is invalid`() {
    }

    @Test
    fun onAttendeeRemoveItemClicked() {
    }
}