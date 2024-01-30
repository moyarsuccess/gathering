package com.gathering.android.auth.password.newPassword

import com.gathering.android.MainDispatcherRule
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.notif.FirebaseRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
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
    fun setUp() {
        MockKAnnotations.init(this)
        sut = InputNewPasswordViewModel(
            repository = authRepository,
            firebaseMessagingRepository = firebaseRepository
        )
    }

    @Test
    fun `onViewCreated sets navigator properly`() {
        // GIVEN
        val inputNewPasswordNavigator = mockk<InputNewPasswordNavigator>()

        //WHEN
        sut.onViewCreated(inputNewPasswordNavigator)

        // THEN
        Assert.assertEquals(inputNewPasswordNavigator, sut.inputNewPasswordNavigator)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSubmitBtnClicked sets uiState inProgress when passwords match`() =
        runTest {
            // GIVEN
            val results = mutableListOf<InputNewPasswordViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.uiState.toList(results)
            }

            // WHEN
            sut.onSubmitBtnClicked(
                "", "tedIsSoSmart1234", "tedIsSoSmart1234"
            )
            runCurrent()

            // THEN
            Assert.assertFalse(results[0].isInProgress)
            Assert.assertTrue(results[1].isInProgress)
            Assert.assertFalse(results[2].isInProgress)
            job.cancel()
        }

    @Test
    fun `onSubmitBtnClicked sets uiState inProgress when passwords DON'T match`() =
        runTest {

        }

    @Test
    fun `onSubmitBtnClicked sets uiState inProgress when token is null or blank`() =
        runTest {

        }

    @Test
    fun `onSubmitBtnClicked sets uiState inProgress when device-token is null or blank`() =
        runTest {

        }

    @Test
    fun `getUiState errorMessage when passwords DON'T match`() =
        runTest { }

    @Test
    fun `onSubmitBtnClicked navigates to Intro screen when passwords match`() =
        runTest { }

    @Test
    fun `onSubmitBtnClicked does NOT navigate to Intro screen when passwords don't match`() =
        runTest { }

    @Test
    fun `onSubmitBtnClicked does NOT navigate to Intro screen if reset password API fails`() =
        runTest { }

}