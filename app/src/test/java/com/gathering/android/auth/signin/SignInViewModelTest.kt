package com.gathering.android.auth.signin

import com.gathering.android.MainDispatcherRule
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.notif.FirebaseRepository
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignInViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    private lateinit var authRepository: AuthRepository

    @MockK(relaxed = true)
    private lateinit var firebaseRepository: FirebaseRepository

    @MockK(relaxed = true)
    private lateinit var validationChecker: ValidationChecker

    private lateinit var classToTest: SignInViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        classToTest = SignInViewModel(authRepository, firebaseRepository, validationChecker)
    }

    @Test
    fun `onViewCreated sets navigator properly`() {
        // GIVEN
        val signInNavigator = mockk<SignInNavigator>()

        //WHEN
        classToTest.onViewCreated(signInNavigator)

        // THEN
        assertEquals(signInNavigator, classToTest.signInNavigator)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignInButtonClicked sets uiState inProgress and error message when email is not correct`() =
        runTest {
            //GIVEN
            every { validationChecker.isEmailValid(any()) } returns false
            every { validationChecker.isPasswordValid(any()) } returns true
            val results = mutableListOf<SignInViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onSignInButtonClicked("animan", "Anahid@12345")
            runCurrent()

            //THEN
            assertFalse(results[0].isInProgress)
            assertEquals(results[0].errorMessage, null)
            assertTrue(results[1].isInProgress)
            assertEquals(results[1].errorMessage, null)
            assertFalse(results[2].isInProgress)
            assertEquals(results[2].errorMessage, "PLEASE ENTER A VALID EMAIL ADDRESS")
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignInButtonClicked sets uiState inProgress and error message when password is not correct`() =
        runTest {
            //GIVEN
            every { validationChecker.isEmailValid(any()) } returns true
            every { validationChecker.isPasswordValid(any()) } returns false
            val results = mutableListOf<SignInViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onSignInButtonClicked("animansoubi@gmail.com", "123445")
            runCurrent()

            //THEN
            assertFalse(results[0].isInProgress)
            assertEquals(results[0].errorMessage, null)
            assertTrue(results[1].isInProgress)
            assertEquals(results[1].errorMessage, null)
            assertFalse(results[2].isInProgress)
            assertEquals(results[2].errorMessage, "PLEASE ENTER A VALID PASSWORD")
            job.cancel()
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignInButtonClicked sets uiState inProgress and error message when device token is null or empty`() =
        runTest {
            //GIVEN
            every { validationChecker.isEmailValid(any()) } returns true
            every { validationChecker.isPasswordValid(any()) } returns true
            coEvery { firebaseRepository.getDeviceToken() } returns null
            val results = mutableListOf<SignInViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onSignInButtonClicked("animansoubi@gmail.com", "123445")
            runCurrent()

            //THEN
            assertFalse(results[0].isInProgress)
            assertEquals(results[0].errorMessage, null)
            assertTrue(results[1].isInProgress)
            assertEquals(results[1].errorMessage, null)
            assertFalse(results[2].isInProgress)
            assertEquals(results[2].errorMessage, "INVALID DEVICE TOKEN")
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignInButtonClicked sets uiState inProgress when email and password are correct and device token is not null or empty`() =
        runTest {
            // GIVEN
            every { validationChecker.isEmailValid(any()) } returns true
            every { validationChecker.isPasswordValid(any()) } returns true
            coEvery { firebaseRepository.getDeviceToken() } returns "valid firebase token"
            val results = mutableListOf<SignInViewModel.UiState>()
            val job =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    classToTest.uiState.toList(results)
                }

            // WHEN
            classToTest.onSignInButtonClicked("asdas@afae.com", "wcewf2d32d#@@D@#")
            runCurrent()

            // THEN
            assertFalse(results[0].isInProgress)
            assertTrue(results[1].isInProgress)
            assertFalse(results[2].isInProgress)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignInButtonClicked should navigate to home screen when email and password are correct and device token is not null or empty`() {
        runTest {
            //GIVEN
            val signInNavigator = mockk<SignInNavigator>()
            classToTest.onViewCreated(signInNavigator)
            every { validationChecker.isEmailValid(any()) } returns true
            every { validationChecker.isPasswordValid(any()) } returns true
            coEvery { firebaseRepository.getDeviceToken() } returns "valid firebase token"

            //WHEN
            classToTest.onSignInButtonClicked("animansoubi@gmail.com", "@nahidM12345")

            //THEN
            coVerify(exactly = 1) { authRepository.signInUser(any(), any(), any()) }
            verify(exactly = 1) { signInNavigator.navigateToHome() }
        }
    }

    @Test
    fun `onSignInButtonClicked does NOT navigate to home if sign in API fails`() =
        runTest {
            //GIVEN
            val signInNavigator = mockk<SignInNavigator>()
            classToTest.onViewCreated(signInNavigator = signInNavigator)
            val e = RuntimeException("random exception")
            every { validationChecker.isEmailValid(any()) } returns true
            every { validationChecker.isPasswordValid(any()) } returns true
            coEvery { firebaseRepository.getDeviceToken() } returns "valid firebase token"
            coEvery { authRepository.signInUser(any(), any(), any()) }.throws(e)

            //WHEN
            classToTest.onSignInButtonClicked("animansoubi@gmail.com", "@nahidM12345")

            //THEN
            coVerify(exactly = 1) { authRepository.signInUser(any(), any(), any()) }
            verify(exactly = 0) { signInNavigator.navigateToHome()}

        }
}