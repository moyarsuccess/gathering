package com.gathering.android.event.putevent.address

import com.gathering.android.MainDispatcherRule
import com.gathering.android.common.toEventLocation
import com.gathering.android.event.LOCATION_IS_NULL_OR_INVALID
import com.gathering.android.event.model.EventLocation
import com.gathering.android.utils.location.LocationHelper
import com.google.android.gms.maps.model.LatLng
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

class AddressViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    private lateinit var locationHelper: LocationHelper

    @MockK(relaxed = true)
    private lateinit var addressNavigator: AddressNavigator

    @MockK(relaxed = true)
    private lateinit var classToTest: AddressViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        classToTest = AddressViewModel(locationHelper)
    }

    @Test
    fun `onViewCreated sets navigator properly`() {
        // WHEN
        classToTest.onViewCreated(EventLocation(0.0, 0.0), addressNavigator)

        // THEN
        Assert.assertEquals(addressNavigator, classToTest.addressNavigator)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onViewCreated set addressText, marker position, and, okButtonEnable when eventLocation is NOT null or invalid`() =
        runTest {
            // GIVEN
            val eventLocation = EventLocation(2.3, 2.5)
            val results = mutableListOf<AddressUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            // WHEN
            classToTest.onViewCreated(eventLocation, addressNavigator = addressNavigator)
            runCurrent()

            // THEN
            Assert.assertEquals(
                locationHelper.addressFromLocation(eventLocation),
                results[1].addressTextValue
            )
            Assert.assertEquals(eventLocation, results[1].markerPosition)
            Assert.assertFalse(results[1].okButtonEnable!!)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onViewCreated set addressText, marker position, and, okButtonEnable when eventLocation is null or invalid`() =
        runTest {
            // GIVEN
            val eventLocation = EventLocation(0.0, 0.0)
            coEvery { locationHelper.getCurrentLocation() } returns mockk(relaxed = true)
            val results = mutableListOf<AddressUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            // WHEN
            classToTest.onViewCreated(eventLocation, addressNavigator = addressNavigator)
            runCurrent()

            // THEN
            Assert.assertEquals(
                locationHelper.addressFromLocation(locationHelper.getCurrentLocation()),
                results[1].addressTextValue
            )
            Assert.assertEquals(locationHelper.getCurrentLocation(), results[1].markerPosition)
            Assert.assertFalse(results[1].okButtonEnable!!)
            Assert.assertEquals(LOCATION_IS_NULL_OR_INVALID, results[1].errorMessage)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onAddressChanged should set addressTextValue appropriately`() =
        runTest {
            // GIVEN
            val results = mutableListOf<AddressUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            // WHEN
            classToTest.onAddressChanged("101 Erskine ave")
            runCurrent()

            // THEN
            Assert.assertEquals("101 Erskine ave", results[1].addressTextValue)
            job.cancel()
        }

    @Test
    fun `onAddressChanged should check suggest address list Not call when address length is less than AUTO_SUGGESTION_THRESH_HOLD`() {
        // WHEN
        classToTest.onAddressChanged("10")

        // THEN
        coVerify(exactly = 0) { locationHelper.suggestAddressList(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onAddressChanged should set suggestedAddressList, okButtonEnable, and, dismissAutoSuggestion when suggested Address is NOT empty`() =
        runTest {
            // GIVEN
            coEvery { locationHelper.suggestAddressList("101 Erskine ave") } returns listOf("101 Erskine ave")

            val results = mutableListOf<AddressUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            // WHEN
            classToTest.onAddressChanged("101 Erskine ave")
            runCurrent()

            // THEN
            Assert.assertEquals(listOf("101 Erskine ave"), results[2].suggestedAddressList)
            Assert.assertTrue(results[2].okButtonEnable!!)
            Assert.assertFalse(results[2].dismissAutoSuggestion)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onClearClicked should set addressTextValue to empty`() =
        runTest {
            // GIVEN
            val results = mutableListOf<AddressUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            // WHEN
            classToTest.onClearClicked()
            runCurrent()

            // THEN
            Assert.assertEquals("", results[1].addressTextValue)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSuggestedAddressClicked should set markerPosition, addressTextValue, and, dismissAutoSuggestion when suggestedAddress is not null`() =
        runTest {
            // GIVEN
            coEvery { locationHelper.locationFromAddress("101 Erskine ave") } returns EventLocation(
                2.3,
                2.5
            )
            val results = mutableListOf<AddressUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            // WHEN
            classToTest.onSuggestedAddressClicked("101 Erskine ave")
            runCurrent()

            // THEN
            Assert.assertEquals(EventLocation(2.3, 2.5), results[1].markerPosition)
            Assert.assertEquals("101 Erskine ave", results[1].addressTextValue)
            Assert.assertTrue(results[1].dismissAutoSuggestion)
            job.cancel()
        }

    @Test
    fun `onOKButtonClicked should navigate to add event`() {
        // GIVEN
        every { classToTest.onViewCreated(EventLocation(2.3, 2.5), addressNavigator) }

        // WHEN
        classToTest.onOKButtonClicked()

        // THEN
        verify(exactly = 1) { addressNavigator.navigateToAddEvent(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onMapLongClicked should set addressTextValue, okButtonEnable, markerPosition, and, dismissAutoSuggestion when address is not empty`() =
        runTest {
            // GIVEN
            coEvery {
                locationHelper.addressFromLocation(
                    LatLng(
                        2.3,
                        2.5
                    ).toEventLocation()
                )
            } returns "101 Erskine ave"
            val results = mutableListOf<AddressUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }
            // WHEN
            classToTest.onMapLongClicked(LatLng(2.3, 2.5))

            // THEN
            Assert.assertEquals("101 Erskine ave", results[1].addressTextValue)
            Assert.assertTrue(results[1].okButtonEnable!!)
            Assert.assertEquals(LatLng(2.3, 2.5).toEventLocation(), results[1].markerPosition)
            Assert.assertTrue(results[1].dismissAutoSuggestion)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onDismissed should set dismissAutoSuggestion to true`() =
        runTest {
            // GIVEN
            val results = mutableListOf<AddressUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }
            // WHEN
            classToTest.onDismissed()
            runCurrent()

            // THEN
            Assert.assertTrue(results[1].dismissAutoSuggestion)
            job.cancel()
        }
}