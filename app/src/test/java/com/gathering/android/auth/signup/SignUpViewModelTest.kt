package com.gathering.android.auth.signup

import com.gathering.android.MainDispatcherRule
import com.gathering.android.auth.AuthException
import com.gathering.android.auth.repo.AuthRepository
import com.gathering.android.auth.signup.SignUpViewModel.Companion.EMAIL_ALREADY_IN_USE
import com.gathering.android.auth.signup.SignUpViewModel.Companion.GENERAL_ERROR
import com.gathering.android.auth.signup.SignUpViewModel.Companion.INVALID_CONFIRMED_PASS
import com.gathering.android.auth.signup.SignUpViewModel.Companion.INVALID_EMAIL_ADDRESS_FORMAT
import com.gathering.android.auth.signup.SignUpViewModel.Companion.INVALID_PASS_FORMAT
import com.gathering.android.common.GeneralApiResponse
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
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignUpViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    private lateinit var authRepository: AuthRepository

    @MockK(relaxed = true)
    private lateinit var firebaseRepository: FirebaseRepository

    @MockK(relaxed = true)
    private lateinit var validationChecker: ValidationChecker

    private lateinit var sut: SignUpViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        sut = SignUpViewModel(
            validationChecker = validationChecker,
            repository = authRepository,
            firebaseMessagingRepository = firebaseRepository
        )
    }

    @Test
    fun `onViewCreated sets navigator properly`() {
        // GIVEN
        val signUpNavigator = mockk<SignUpNavigator>()

        //WHEN
        sut.onViewCreated(signUpNavigator)

        // THEN
        Assert.assertEquals(signUpNavigator, sut.signUpNavigator)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignUpButtonClicked sets uiState inProgress when email and passwords are correct`() =
        runTest {
            //GIVEN
            every { validationChecker.isEmailValid(any()) } returns true
            every { validationChecker.isPasswordValid(any()) } returns true
            every { validationChecker.isConfirmedPassValid(any(), any()) } returns true
            val results = mutableListOf<SignUpViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.uiState.toList(results)
            }

            //WHEN
            sut.onSignUpButtonClicked(
                "tedmosby@gmail.com", "tedIsSoSmart1234", "tedIsSoSmart1234"
            )
            runCurrent()

            //THEN
            Assert.assertFalse(results[0].isInProgress)
            Assert.assertTrue(results[1].isInProgress)
            Assert.assertFalse(results[2].isInProgress)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignUpButtonClicked sets uiState errorMessage when passwords provided but email is NOT correct`() =
        runTest {
            //GIVEN
            every { validationChecker.isEmailValid(any()) } returns false
            every { validationChecker.isPasswordValid(any()) } returns true
            every { validationChecker.isConfirmedPassValid(any(), any()) } returns true
            val results = mutableListOf<SignUpViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.uiState.toList(results)
            }

            //WHEN
            sut.onSignUpButtonClicked(
                "tedMosby.comm", "tedIsSoSmart1234", "tedIsSoSmart1234"
            )
            runCurrent()

            //THEN
            Assert.assertEquals(results[0].errorMessage, null)
            Assert.assertEquals(results[1].errorMessage, null)
            Assert.assertEquals(results[2].errorMessage, INVALID_EMAIL_ADDRESS_FORMAT)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignUpButtonClicked sets uiState errorMessage when email is correct but passwords DONT match`() =
        runTest {
            //GIVEN
            every { validationChecker.isEmailValid(any()) } returns true
            every { validationChecker.isPasswordValid(any()) } returns true
            every { validationChecker.isConfirmedPassValid(any(), any()) } returns false
            val results = mutableListOf<SignUpViewModel.UiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.uiState.toList(results)
            }

            //WHEN
            sut.onSignUpButtonClicked(
                "tedmosby@gmail.com", "12345aA#", "12345aA###"
            )
            runCurrent()

            //THEN
            Assert.assertEquals(sut.uiState.value.errorMessage, INVALID_CONFIRMED_PASS)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignUpButtonClicked sets uiState errorMessage when email is already in use`() =
        runTest {
            //GIVEN
            every { validationChecker.isEmailValid(any()) } returns true
            every { validationChecker.isPasswordValid(any()) } returns true
            every { validationChecker.isConfirmedPassValid(any(), any()) } returns true
            coEvery { firebaseRepository.getDeviceToken() } returns "valid_device_token"
            coEvery {
                authRepository.signUpUser(
                    email = any(),
                    pass = any(),
                    deviceToken = "valid_device_token"
                )
            } throws AuthException.EmailAlreadyInUseException

            //WHEN
            sut.onSignUpButtonClicked(
                "tedmosby@gmail.com", "12345aA#", "12345aA###"
            )
            runCurrent()

            //THEN
            Assert.assertEquals(sut.uiState.value.errorMessage, EMAIL_ALREADY_IN_USE)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignUpButtonClicked sets uiState errorMessage when there is a random error`() = runTest {
        //GIVEN
        val e = Exception("random error")
        every { validationChecker.isEmailValid(any()) } returns true
        every { validationChecker.isPasswordValid(any()) } returns true
        every { validationChecker.isConfirmedPassValid(any(), any()) } returns true
        coEvery { firebaseRepository.getDeviceToken() } returns "valid_device_token"
        coEvery {
            authRepository.signUpUser(
                email = any(), pass = any(), deviceToken = "valid_device_token"
            )
        } throws e

        //WHEN
        sut.onSignUpButtonClicked(
            "tedmosby@gmail.com", "12345aA#", "12345aA###"
        )
        runCurrent()

        //THEN
        Assert.assertEquals(sut.uiState.value.errorMessage, GENERAL_ERROR)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignUpButtonClicked sets uiState errorMessage when email is correct but password fields are empty`() =
        runTest {
            //GIVEN
            every { validationChecker.isEmailValid(any()) } returns true
            every { validationChecker.isPasswordValid(any()) } returns false
            every { validationChecker.isConfirmedPassValid(any(), any()) } returns false

            //WHEN
            sut.onSignUpButtonClicked(
                "tedmosby@gmail.com", "", ""
            )
            runCurrent()

            //THEN
            Assert.assertEquals(sut.uiState.value.errorMessage, INVALID_PASS_FORMAT)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignUpButtonClicked sets uiState errorMessage when passwords correct but email field is EMPTY`() =
        runTest {
            //GIVEN
            every { validationChecker.isEmailValid(any()) } returns false
            every { validationChecker.isPasswordValid(any()) } returns true
            every { validationChecker.isConfirmedPassValid(any(), any()) } returns true

            //WHEN
            sut.onSignUpButtonClicked(
                "", "12345aA#", "12345aA#"
            )
            runCurrent()

            //THEN
            Assert.assertEquals(sut.uiState.value.errorMessage, INVALID_EMAIL_ADDRESS_FORMAT)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSignUpButtonClicked sets uiState errorMessage device token is invalid`() = runTest {
        //GIVEN
        every { validationChecker.isEmailValid(any()) } returns true
        every { validationChecker.isPasswordValid(any()) } returns true
        every { validationChecker.isConfirmedPassValid(any(), any()) } returns true

        //WHEN
        sut.onSignUpButtonClicked(
            "tedmosby@gmail.com", "12345aA#", "12345aA#"
        )
        runCurrent()

        //THEN
        Assert.assertEquals(sut.uiState.value.errorMessage, "INVALID_DEVICE_TOKEN")
    }

    @Test
    fun `onSignUpButtonClicked navigates to verification screen if email and passwords are valid`() =
        runTest {
            // GIVEN
            val signUpNavigator = mockk<SignUpNavigator>()
            sut.onViewCreated(signUpNavigator)
            coEvery { validationChecker.isEmailValid(any()) } returns true
            coEvery { validationChecker.isPasswordValid(any()) } returns true
            coEvery {
                validationChecker.isConfirmedPassValid(
                    "Password123", "Password123"
                )
            } returns true
            coEvery { firebaseRepository.getDeviceToken() } returns "VALID_DEVICE_TOKEN"

            // WHEN
            sut.onSignUpButtonClicked("test@example.com", "Password123", "Password123")

            // THEN
            coEvery {
                authRepository.signUpUser(
                    "Password123",
                    "Password123",
                    any()
                )
            } coAnswers { GeneralApiResponse("") }
            verify { signUpNavigator.navigateToVerification(any()) }
        }

    @Test
    fun `onSignUpButtonClicked does NOT navigate to verification screen if email is empty`() =
        runTest {
            //GIVEN
            val signUpNavigator = mockk<SignUpNavigator>()
            sut.onViewCreated(signUpNavigator)

            //WHEN
            sut.onSignUpButtonClicked(
                email = "", pass = "12345aA#", confirmPass = "12345aA#"
            )

            //THEN
            verify(exactly = 0) { signUpNavigator.navigateToVerification("") }
            coVerify(exactly = 0) { authRepository.signUpUser(any(), any(), any()) }
        }

    @Test
    fun `onSignUpButtonClicked does NOT navigate to verification screen if password field is empty`() =
        runTest {
            //GIVEN
            val signUpNavigator = mockk<SignUpNavigator>()
            sut.onViewCreated(signUpNavigator)

            //WHEN
            sut.onSignUpButtonClicked(
                email = "tedmosby@gmail.com", pass = "", confirmPass = "12345aA#"
            )

            //THEN
            verify(exactly = 0) { signUpNavigator.navigateToVerification("") }
            coVerify(exactly = 0) { authRepository.signUpUser(any(), any(), any()) }
        }

    @Test
    fun `onSignUpButtonClicked does NOT navigate to verification screen if confirmed pass field is empty`() =
        runTest {
            //GIVEN
            val signUpNavigator = mockk<SignUpNavigator>()
            sut.onViewCreated(signUpNavigator)

            //WHEN
            sut.onSignUpButtonClicked(
                email = "tedmosby@gmail.com", pass = "12345aA#", confirmPass = ""
            )
            //THEN
            verify(exactly = 0) { signUpNavigator.navigateToVerification("") }
            coVerify(exactly = 0) { authRepository.signUpUser(any(), any(), any()) }
        }

    @Test
    fun `onSignUpButtonClicked does NOT navigate to verification screen if sign up API fails`() =
        runTest {
            // GIVEN
            val signUpNavigator = mockk<SignUpNavigator>()
            sut.onViewCreated(signUpNavigator)
            coEvery { validationChecker.isEmailValid(any()) } returns true
            coEvery { validationChecker.isPasswordValid(any()) } returns true
            coEvery {
                validationChecker.isConfirmedPassValid(
                    any(),
                    any()
                )
            } returns true
            coEvery { firebaseRepository.getDeviceToken() } returns "valid_device_token"
            val exception = RuntimeException("API failure")
            coEvery { authRepository.signUpUser(any(), any(), any()) } throws exception

            // WHEN
            sut.onSignUpButtonClicked("", "password", "confirmPassword")

            // THEN
            coVerify { authRepository.signUpUser(any(), any(), any()) }
            verify(exactly = 0) { signUpNavigator.navigateToVerification(any()) }
        }

    @Test
    fun `onSignUpButtonClicked does NOT navigate to verification screen if email is NOT valid`() =
        runTest {
            //GIVEN
            val signUpNavigator = mockk<SignUpNavigator>()
            sut.onViewCreated(signUpNavigator)

            //WHEN
            sut.onSignUpButtonClicked("amir.com", "12345aA#", "12345aA#")

            //THEN
            verify(exactly = 0) { signUpNavigator.navigateToVerification("amir.com") }
            coVerify(exactly = 0) { authRepository.signUpUser(any(), any(), any()) }
        }

    @Test
    fun `onSignUpButtonClicked does NOT navigate to verification screen if password is NOT valid`() =
        runTest {
            //GIVEN
            val signUpNavigator = mockk<SignUpNavigator>()
            sut.onViewCreated(signUpNavigator)

            //WHEN
            sut.onSignUpButtonClicked("tedmosby@gmail.com", "12345aA##", "12345aA#")

            //THEN
            verify(exactly = 0) { signUpNavigator.navigateToVerification("tedmosby@gmail.com") }
            coVerify(exactly = 0) { authRepository.signUpUser(any(), any(), any()) }
        }

    @Test
    fun `onSignUpButtonClicked does NOT navigate to verification screen if confirm password is NOT valid`() =
        runTest {
            //GIVEN
            val signUpNavigator = mockk<SignUpNavigator>()
            sut.onViewCreated(signUpNavigator)

            //WHEN
            sut.onSignUpButtonClicked("tedmosby@gmail.com", "12345aA#", "12345aA##")

            //THEN
            verify(exactly = 0) { signUpNavigator.navigateToVerification("tedmosby@gmail.com") }
            coVerify(exactly = 0) { authRepository.signUpUser(any(), any(), any()) }
        }
}