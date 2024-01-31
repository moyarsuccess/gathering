package com.gathering.android.event.putevent.invitation

import com.gathering.android.MainDispatcherRule
import com.gathering.android.event.putevent.invitation.AddAttendeesViewModel.Companion.EMAIL_IS_NOT_VALID
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
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
    fun `onAddAttendeeButtonClicked sets uiState error message when email field is empty`() {
        // GIVEN
        val attendees = ""

        // WHEN
        sut.onAddAttendeeButtonClicked(attendees)

        // THEN
        Assert.assertEquals(
            sut.uiState.value.errorMessage, AddAttendeesViewModel.EMAIL_IS_NOT_VALID
        )
    }

    @Test
    fun `onAddAttendeeButtonClicked sets null uiState errorMessage when email is correct`() {
        // GIVEN
        val attendees = "test1@example.com"

        // WHEN
        sut.onAddAttendeeButtonClicked(attendees)

        // THEN
        Assert.assertEquals(
            sut.uiState.value.errorMessage, ""
        )
    }

    @Test
    fun `onAttendeeEmailChanged updates state correctly for valid email`() {
        // GIVEN
        val attendees = "email1,email2,email3"
        sut.onViewCreated(attendees, addAttendeeNavigator)

        // WHEN
        sut.onAttendeeEmailChanged("newEmail@example.com")

        // THEN
        val currentState = sut.uiState.value
        Assert.assertEquals("newEmail@example.com", currentState.attendeeEmail)
        Assert.assertTrue(currentState.addAttendeeButtonEnable)
        Assert.assertEquals("", currentState.errorMessage)
    }

    @Test
    fun `onAttendeeEmailChanged updates state correctly for invalid email`() {
        // GIVEN
        val attendees = "email1,email2,email3"
        sut.onViewCreated(attendees, addAttendeeNavigator)

        // WHEN
        sut.onAttendeeEmailChanged("invalidEmail")

        // THEN
        val currentState = sut.uiState.value
        Assert.assertEquals("invalidEmail", currentState.attendeeEmail)
        Assert.assertFalse(currentState.addAttendeeButtonEnable)
        Assert.assertEquals("", currentState.errorMessage)
    }
    @Test
    fun `onAttendeeEmailChanged does not let duplicate emails to be added`() {
        // GIVEN
        val attendees = "email1,email2,email3"
        sut.onViewCreated(attendees, addAttendeeNavigator)

        // WHEN
        sut.onAddAttendeeButtonClicked("email1")

        // THEN
        val currentState = sut.uiState.value
        Assert.assertEquals("", currentState.attendeeEmail)
        Assert.assertFalse(currentState.addAttendeeButtonEnable)
        Assert.assertEquals(currentState.errorMessage, EMAIL_IS_NOT_VALID)
    }
}