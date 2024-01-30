package com.gathering.android.event.putevent.invitation

import com.gathering.android.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
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
        val addAttendeeNavigator = mockk<AddAttendeeNavigator>()

        //WHEN
        sut.onViewCreated(addAttendeeNavigator = addAttendeeNavigator, attendees = "")

        // THEN
        Assert.assertEquals(addAttendeeNavigator, sut.addAttendeeNavigator)
    }

    @Test
    fun onOKButtonClicked() {
    }

    @Test
    fun onAttendeeEmailChanged() {
    }

    @Test
    fun onAddAttendeeButtonClicked() {
    }

    @Test
    fun onAttendeeRemoveItemClicked() {
    }
}