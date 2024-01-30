package com.gathering.android.auth.password.newPassword

import com.gathering.android.MainDispatcherRule
import com.gathering.android.auth.AuthException
import com.gathering.android.auth.password.newPassword.InputNewPasswordViewModel.Companion.CAN_NOT_REACH_SERVER
import com.gathering.android.auth.password.newPassword.InputNewPasswordViewModel.Companion.GENERAL_ERROR
import com.gathering.android.auth.password.newPassword.InputNewPasswordViewModel.Companion.INVALID_DEVICE_TOKEN
import com.gathering.android.auth.password.newPassword.InputNewPasswordViewModel.Companion.LINK_NOT_VALID
import com.gathering.android.auth.password.newPassword.InputNewPasswordViewModel.Companion.PASSWORDS_DO_NOT_MATCH
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.notif.FirebaseRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
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

class InputNewPasswordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    private lateinit var authRepository: AuthRepository

    @MockK(relaxed = true)
    private lateinit var firebaseRepository: FirebaseRepository

    private lateinit var sut: InputNewPasswordViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        sut = InputNewPasswordViewModel(
            repository = authRepository, firebaseMessagingRepository = firebaseRepository
        )
    }

    @Test
    fun `onViewCreated sets navigator properly`() {
        // GIVEN
        val inputNewPasswordNavigator = mockk<InputNewPasswordNavigator>()

        // WHEN
        sut.onViewCreated(inputNewPasswordNavigator)

        // THEN
        Assert.assertEquals(inputNewPasswordNavigator, sut.inputNewPasswordNavigator)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSubmitBtnClicked sets uiState inProgress when token, newPassword, and confirmPassword are correct`() =
        runTest {
            // GIVEN
            coEvery { firebaseRepository.getDeviceToken() } returns "valid_device_token"
            coEvery {
                authRepository.resetPassword(
                    password = any(), token = any(), deviceToken = "valid_device_token"
                )
            }
            val results = mutableListOf<InputNewPasswordViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.uiState.toList(results)
            }

            // WHEN
            sut.onSubmitBtnClicked("valid_token", "newPassword", "newPassword")
            runCurrent()

            // THEN
            Assert.assertFalse(results[0].isInProgress)
            Assert.assertTrue(results[1].isInProgress)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSubmitBtnClicked sets uiState inProgress when token is incorrect but newPassword, and confirmPassword are correct`() =
        runTest {
            // GIVEN
            coEvery { firebaseRepository.getDeviceToken() } returns "invalid_device_token"
            coEvery {
                authRepository.resetPassword(
                    password = any(), token = any(), deviceToken = "invalid_device_token"
                )
            }
            val results = mutableListOf<InputNewPasswordViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.uiState.toList(results)
            }

            // WHEN
            sut.onSubmitBtnClicked("invalid_token", "newPassword", "newPassword")
            runCurrent()

            // THEN
            Assert.assertFalse(results[0].isInProgress)
            Assert.assertTrue(results[1].isInProgress)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSubmitBtnClicked sets uiState errorMessage when token is null or blank`() = runTest {
        // GIVEN
        coEvery { firebaseRepository.getDeviceToken() } returns ""
        coEvery {
            authRepository.resetPassword(
                password = any(), token = any(), deviceToken = ""
            )
        }

        // WHEN
        sut.onSubmitBtnClicked(null, "newPassword", "newPassword")
        runCurrent()

        // THEN
        Assert.assertEquals(
            sut.uiState.value.errorMessage, LINK_NOT_VALID
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSubmitBtnClicked sets uiState errorMessage when newPassword and confirmPassword do not match`() =
        runTest {
            // GIVEN
            coEvery {
                authRepository.resetPassword(
                    password = any(), token = any(), deviceToken = ""
                )
            }

            // WHEN
            sut.onSubmitBtnClicked("valid_token", "newPassword", "differentPassword")
            runCurrent()

            // THEN
            Assert.assertEquals(
                sut.uiState.value.errorMessage, PASSWORDS_DO_NOT_MATCH
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSubmitBtnClicked sets uiState errorMessage when device token is invalid`() = runTest {
        // GIVEN
        coEvery { firebaseRepository.getDeviceToken() } returns null

        // WHEN
        sut.onSubmitBtnClicked("valid_token", "newPassword", "newPassword")
        runCurrent()

        // THEN
        Assert.assertEquals(
            sut.uiState.value.errorMessage, INVALID_DEVICE_TOKEN
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSubmitBtnClicked sets uiState errorMessage when reset password API fails`() = runTest {
        // GIVEN
        coEvery { firebaseRepository.getDeviceToken() } returns "valid_device_token"
        coEvery {
            authRepository.resetPassword(
                password = any(), token = any(), deviceToken = "valid_device_token"
            )
        } throws AuthException.FailedConnectingToServerException

        // WHEN
        sut.onSubmitBtnClicked("valid_token", "newPassword", "newPassword")
        runCurrent()

        // THEN
        Assert.assertEquals(sut.uiState.value.errorMessage, CAN_NOT_REACH_SERVER)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSubmitBtnClicked sets uiState errorMessage in case of reset password API failure`() =
        runTest {
            // GIVEN
            val inputNewPasswordNavigator = mockk<InputNewPasswordNavigator>()
            sut.onViewCreated(inputNewPasswordNavigator)
            coEvery { firebaseRepository.getDeviceToken() } returns "valid_device_token"
            coEvery {
                authRepository.resetPassword(
                    password = any(), token = any(), deviceToken = "valid_device_token"
                )
            } throws RuntimeException("API failure")

            // WHEN
            sut.onSubmitBtnClicked("valid_token", "newPassword", "newPassword")
            runCurrent()

            // THEN
            Assert.assertEquals(sut.uiState.value.errorMessage, GENERAL_ERROR)
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSubmitBtnClicked sets uiState errorMessage when there is a general error`() = runTest {
        // GIVEN
        val exception = RuntimeException("Ooops. something Wrong!")
        coEvery { firebaseRepository.getDeviceToken() } returns "valid_device_token"
        coEvery {
            authRepository.resetPassword(
                password = any(), token = any(), deviceToken = "valid_device_token"
            )
        } throws exception

        // WHEN
        sut.onSubmitBtnClicked("valid_token", "newPassword", "newPassword")
        runCurrent()

        // THEN
        Assert.assertEquals(sut.uiState.value.errorMessage, GENERAL_ERROR)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSubmitBtnClicked navigates to Intro screen when passwords match`() = runTest {
        // GIVEN
        val inputNewPasswordNavigator = mockk<InputNewPasswordNavigator>()
        sut.onViewCreated(inputNewPasswordNavigator)
        coEvery { firebaseRepository.getDeviceToken() } returns "valid_device_token"
        coEvery {
            authRepository.resetPassword(
                password = any(), token = any(), deviceToken = "valid_device_token"
            )
        }

        // WHEN
        sut.onSubmitBtnClicked("valid_device_token", "newPassword", "newPassword")
        runCurrent()

        // THEN
        verify(exactly = 0) { inputNewPasswordNavigator.navigateToIntroFragment() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSubmitBtnClicked does NOT navigate to Intro screen when passwords don't match`() =
        runTest {
            // GIVEN
            val inputNewPasswordNavigator = mockk<InputNewPasswordNavigator>()
            sut.onViewCreated(inputNewPasswordNavigator)

            // WHEN
            sut.onSubmitBtnClicked("valid_token", "newPassword", "differentPassword")
            runCurrent()

            // THEN
            verify(exactly = 0) { inputNewPasswordNavigator.navigateToIntroFragment() }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSubmitBtnClicked does NOT navigate to Intro screen if reset password API fails`() =
        runTest {
            // GIVEN
            val inputNewPasswordNavigator = mockk<InputNewPasswordNavigator>()
            sut.onViewCreated(inputNewPasswordNavigator)
            coEvery { firebaseRepository.getDeviceToken() } returns "valid_device_token"
            coEvery {
                authRepository.resetPassword(
                    password = any(), token = any(), deviceToken = "valid_device_token"
                )
            } throws AuthException.FailedConnectingToServerException

            // WHEN
            sut.onSubmitBtnClicked("valid_token", "newPassword", "newPassword")
            runCurrent()

            // THEN
            verify(exactly = 0) { inputNewPasswordNavigator.navigateToIntroFragment() }
        }
}