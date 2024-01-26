package com.gathering.android.auth.password.forgetPassword

import com.gathering.android.MainDispatcherRule
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.utils.ValidationChecker
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ForgetPasswordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    private lateinit var authRepository: AuthRepository

    @MockK(relaxed = true)
    private lateinit var validationChecker: ValidationChecker

    private lateinit var classToTest: ForgetPasswordViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        classToTest = ForgetPasswordViewModel(authRepository, validationChecker)
    }

    @Test
    fun `onViewCreated sets navigator properly`() {
        // GIVEN
        val forgetPasswordNavigator = mockk<ForgetPasswordNavigator>()

        //WHEN
        classToTest.onViewCreated(forgetPasswordNavigator)

        // THEN
        Assert.assertEquals(forgetPasswordNavigator, classToTest.forgetPasswordNavigator)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSendLinkBtnClicked sets uiState inProgress when email is correct`() =
        runTest {
            //GIVEN
            every { validationChecker.isEmailValid(any()) } returns true
            val results = mutableListOf<ForgetPasswordViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onSendLinkBtnClicked("amir@ziarati.com")
            runCurrent()

            //THEN
            assertFalse(results[0].isInProgress)
            assertTrue(results[1].isInProgress)
            assertFalse(results[2].isInProgress)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSendLinkBtnClicked sets uiState inProgress when email is NOT correct`() =
        runTest {
            //GIVEN
            every { validationChecker.isEmailValid(any()) } returns false
            val results = mutableListOf<ForgetPasswordViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onSendLinkBtnClicked("amir@ziarati.com")
            runCurrent()

            //THEN
            assertFalse(results[0].isInProgress)
            assertTrue(results[1].isInProgress)
            assertFalse(results[2].isInProgress)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSendLinkBtnClicked sets uiState error message when email is NOT correct`() =
        runTest {
            //GIVEN
            every { validationChecker.isEmailValid(any()) } returns false
            val results = mutableListOf<ForgetPasswordViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onSendLinkBtnClicked("")
            runCurrent()

            //THEN
            assertEquals(results[0].errorMessage, null)
            assertEquals(results[1].errorMessage, null)
            assertEquals(results[2].errorMessage, "INVALID EMAIL ADDRESS")
            job.cancel()
        }

    @Test
    fun `onSendLinkBtnClicked does NOT navigate to reset info bottom sheet if email is empty`() =
        runTest {
            //GIVEN
            val forgetPasswordNavigator = mockk<ForgetPasswordNavigator>()
            classToTest.onViewCreated(forgetPasswordNavigator)

            //WHEN
            classToTest.onSendLinkBtnClicked("")

            //THEN
            verify(exactly = 0) { forgetPasswordNavigator.navigateToResetPassInfoBottomSheet() }
            coVerify(exactly = 0) { authRepository.forgetPassword(any()) }
        }

    @Test
    fun `onSendLinkBtnClicked does NOT navigate to reset info bottom sheet if email is NOT valid`() =
        runTest {
            //GIVEN
            val forgetPasswordNavigator = mockk<ForgetPasswordNavigator>()
            classToTest.onViewCreated(forgetPasswordNavigator)

            //WHEN
            classToTest.onSendLinkBtnClicked("amir.com")

            //THEN
            verify(exactly = 0) { forgetPasswordNavigator.navigateToResetPassInfoBottomSheet() }
            coVerify(exactly = 0) { authRepository.forgetPassword(any()) }
        }

    @Test
    fun `onSendLinkBtnClicked does NOT navigate to reset info bottom sheet if forget password API fails`() =
        runTest {
            //GIVEN
            val forgetPasswordNavigator = mockk<ForgetPasswordNavigator>()
            classToTest.onViewCreated(forgetPasswordNavigator)
            val e = RuntimeException("random exception")
            every { validationChecker.isEmailValid(any()) } returns true
            coEvery { authRepository.forgetPassword(any()) }.throws(e)

            //WHEN
            classToTest.onSendLinkBtnClicked("amir@gmail.com")

            //THEN
            coVerify(atMost = 1) { authRepository.forgetPassword(any()) }
            verify(exactly = 0) { forgetPasswordNavigator.navigateToResetPassInfoBottomSheet() }
        }

    @Test
    fun `onSendLinkBtnClicked navigates to reset info bottom sheet if email is valid`() =
        runTest {
            //GIVEN
            val forgetPasswordNavigator = mockk<ForgetPasswordNavigator>()
            every { validationChecker.isEmailValid(any()) } returns true
            classToTest.onViewCreated(forgetPasswordNavigator)

            //WHEN
            classToTest.onSendLinkBtnClicked("amir@gmail.com")

            //THEN
            verify(exactly = 1) { forgetPasswordNavigator.navigateToResetPassInfoBottomSheet() }
            coVerify(exactly = 1) { authRepository.forgetPassword(any()) }
        }
}