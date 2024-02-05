package com.gathering.android.event.putevent.pic

import android.content.Intent
import android.graphics.Bitmap
import com.gathering.android.MainDispatcherRule
import com.gathering.android.event.putevent.pic.AddPicViewModel.Companion.FAILED_TO_LOAD_IMAGE
import io.mockk.MockKAnnotations
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

class AddPicViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    private lateinit var bitmapUtility: BitmapUtility

    @MockK(relaxed = true)
    private lateinit var addPicNavigator: AddPicNavigator

    private lateinit var classToTest: AddPicViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        classToTest = AddPicViewModel(bitmapUtility)
    }

    @Test
    fun `onViewCreated sets navigator properly`() {
        // WHEN
        classToTest.onViewCreated(addPicNavigator)

        // THEN
        Assert.assertEquals(addPicNavigator, classToTest.addPicNavigator)
    }

    @Test
    fun `onCameraClicked should navigate to camera`() {
        //GIVEN
        classToTest.onViewCreated(addPicNavigator)

        //WHEN
        classToTest.onCameraClicked()

        //THEN
        verify(exactly = 1) { addPicNavigator.navigateToCamera() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onImageSelectedFromCamera should set show image state`() =
        runTest {
            //GIVEN
            val bitmap = mockk<Bitmap>()
            val results = mutableListOf<AddPicViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onImageSelectedFromCamera(bitmap)

            //THEN
            Assert.assertEquals(bitmap, results[1].showImage)
            job.cancel()
        }

    @Test
    fun `onGalleryClicked should navigate to gallery`() {
        //GIVEN
        classToTest.onViewCreated(addPicNavigator)

        //WHEN
        classToTest.onGalleryClicked()

        //THEN
        verify(exactly = 1) { addPicNavigator.navigateToGallery() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onImageSelectedFromGallery should set show image state when data is Not null`() =
        runTest {
            //GIVEN
            val intent = mockk<Intent>(relaxed = true)
            val image = bitmapUtility.loadBitmap(intent.data ?: mockk())
            val results = mutableListOf<AddPicViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onImageSelectedFromGallery(intent)
            runCurrent()

            //THEN
            Assert.assertEquals(image, results[1].showImage)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onImageSelectedFromGallery should set error message state when data is null`() =
        runTest {
            val intent = mockk<Intent>(relaxed = true)
            every { bitmapUtility.loadBitmap(intent.data ?: mockk()) } returns null
            val results = mutableListOf<AddPicViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onImageSelectedFromGallery(intent)
            runCurrent()

            //THEN
            Assert.assertEquals(FAILED_TO_LOAD_IMAGE, results[1].errorMessage)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onRotateClicked should call rotateBitmap and set show image state`() =
        runTest {
            //GIVEN
            val bitmap = mockk<Bitmap>()
            val rotatedBitmap = mockk<Bitmap>()
            every { bitmapUtility.rotateBitmap(bitmap, 90F) } returns rotatedBitmap
            val results = mutableListOf<AddPicViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onRotateClicked(bitmap, 90F)

            //THEN
            verify(exactly = 1) { bitmapUtility.rotateBitmap(bitmap, 90F) }
            Assert.assertEquals(rotatedBitmap, results[1].showImage)
            job.cancel()
        }

    @Test
    fun `onSaveButtonClicked should call store bitmap and navigate to add event`() {
        //GIVEN
        classToTest.onViewCreated(addPicNavigator)
        val bitmap = mockk<Bitmap>()
        every { bitmapUtility.storeBitmapAndGetPath(bitmap) } returns "https://image.jpg"

        //WHEN
        classToTest.onSaveButtonClicked(bitmap)

        //THEN
        verify(exactly = 1) { bitmapUtility.storeBitmapAndGetPath(bitmap) }
        verify(exactly = 1) { addPicNavigator.navigateToAddEvent("https://image.jpg") }
    }
}