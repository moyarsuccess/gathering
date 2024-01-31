package com.gathering.android.profile.editProfile

import com.gathering.android.MainDispatcherRule
import com.gathering.android.auth.model.User
import com.gathering.android.common.UserRepository
import com.gathering.android.profile.editProfile.EditProfileViewModel.Companion.DISPLAY_NAME_NOT_FILLED_MESSAGE
import com.gathering.android.profile.editProfile.EditProfileViewModel.Companion.IMAGE_NOT_FILLED_MESSAGE
import com.gathering.android.profile.repo.ProfileRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
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

class EditProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    lateinit var userRepository: UserRepository

    @MockK(relaxed = true)
    lateinit var profileRepository: ProfileRepository

    @MockK(relaxed = true)
    private lateinit var editProfileNavigator: EditProfileNavigator

    private lateinit var classToTest: EditProfileViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        classToTest = EditProfileViewModel(userRepository, profileRepository)
    }

    @Test
    fun `onViewCreated set navigator property`() {
        //WHEN
        classToTest.onViewCreated(editProfileNavigator)

        //THEN
        Assert.assertEquals(editProfileNavigator, classToTest.editProfileNavigator)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onViewCreated set state appropriately when the User is not null`() =
        runTest {
            //GIVEN
            coEvery { userRepository.getUser() } returns User(
                displayName = "Ani",
                email = "animansoubi@gamil.com"
            )
            val results = mutableListOf<EditProfileUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onViewCreated(editProfileNavigator)
            runCurrent()

            //THEN
            Assert.assertEquals(null, results[0].imageUri)
            Assert.assertEquals(null, results[0].displayName)
            Assert.assertEquals(false, results[0].saveButtonEnable)
            Assert.assertEquals(null, results[0].email)
            Assert.assertEquals("https://moyar.dev:8080/photo/", results[1].imageUri)
            Assert.assertEquals("Ani", results[1].displayName)
            Assert.assertEquals("animansoubi@gamil.com", results[1].email)
            Assert.assertEquals(false, results[1].saveButtonEnable)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onViewCreated should update state appropriately when get User API fails`() =
        runTest {
            // GIVEN
            val e = RuntimeException("random exception")
            coEvery { userRepository.getUser() }.throws(e)

            val results = mutableListOf<EditProfileUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onViewCreated(editProfileNavigator)
            runCurrent()

            //THEN
            Assert.assertEquals(null, results[0].imageUri)
            Assert.assertEquals(null, results[0].displayName)
            Assert.assertEquals(null, results[0].email)
            Assert.assertEquals(false, results[0].saveButtonEnable)
            job.cancel()
        }

    @Test
    fun `onImageButtonClicked should navigate to add pic`() {
        //GIVEN
        classToTest.onViewCreated(editProfileNavigator)

        //WHEN
        classToTest.onImageButtonClicked()

        //THEN
        verify(exactly = 1) { editProfileNavigator.navigateToAddPic() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onImageURLChanged should update state when imageUrl is not empty`() =
        runTest {
            //GIVEN
            val results = mutableListOf<EditProfileUiState>()
            val job =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    classToTest.uiState.toList(results)
                }

            //WHEN
            classToTest.onImageURLChanged("https://moyar.dev:8080/photo/")
            runCurrent()

            //THEN
            Assert.assertEquals(null, results[0].imageUri)
            Assert.assertEquals("https://moyar.dev:8080/photo/", results[1].imageUri)
            Assert.assertEquals(null, results[0].errorMessage)
            Assert.assertEquals(null, results[0].errorMessage)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onImageURLChanged should set errorMessage when imageUrl is empty`() =
        runTest {
            //GIVEN
            val results = mutableListOf<EditProfileUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onImageURLChanged("")
            runCurrent()

            //THEN
            Assert.assertEquals(IMAGE_NOT_FILLED_MESSAGE, results[1].errorMessage)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onImageURLChanged should set null error message and image url state when imageUrl is not empty`() =
        runTest {
            //GIVEN
            val results = mutableListOf<EditProfileUiState>()
            val job =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    classToTest.uiState.toList(results)
                }

            //WHEN
            classToTest.onImageURLChanged("https://moyar.dev:8080/photo/")
            runCurrent()

            //THEN
            Assert.assertEquals("https://moyar.dev:8080/photo/", results[1].imageUri)
            Assert.assertEquals(null, results[1].errorMessage)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onDisplayNameChanged should update state when is display name is empty`() =
        runTest {
            //GIVEN
            val results = mutableListOf<EditProfileUiState>()
            val job =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    classToTest.uiState.toList(results)
                }

            //WHEN
            classToTest.onDisplayNameChanged("")
            runCurrent()

            //THEN
            Assert.assertEquals(DISPLAY_NAME_NOT_FILLED_MESSAGE, results[1].errorMessage)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onDisplayNameChanged should update state when is display name is not empty or null`() =
        runTest {
            //GIVEN
            val results = mutableListOf<EditProfileUiState>()
            val job =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    classToTest.uiState.toList(results)
                }

            //WHEN
            classToTest.onDisplayNameChanged("Ani Mansoubi")
            runCurrent()

            //THEN
            Assert.assertEquals("Ani Mansoubi", results[1].displayName)
            Assert.assertEquals(null, results[1].errorMessage)
            job.cancel()
        }

    @Test
    fun `onSaveButtonClicked should NOT call profile API when displayName or imageUrl is empty or null`() {
        //GIVEN
        classToTest.onViewCreated(editProfileNavigator)

        //WHEN
        classToTest.onSaveButtonClicked("", "")

        //THEN
        coVerify(exactly = 0) { profileRepository.updateProfile(any(), any()) }
    }

    @Test
    fun `onSaveButtonClicked should call profile API when displayName or imageUrl is NOT empty or null`() {
        //WHEN
        classToTest.onSaveButtonClicked("animan", "")

        //THEN
        coVerify(exactly = 1) { profileRepository.updateProfile("animan", "") }
    }

    @Test
    fun `onSaveButtonClicked should set displayName and imageURL state when displayName or imageURL  is NOT empty or null`() =
        runTest {
            //GIVEN
            val results = mutableListOf<EditProfileUiState>()
            val job =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    classToTest.uiState.toList(results)
                }

            //WHEN
            classToTest.onSaveButtonClicked("animan", "https://moyar.dev:8080/photo/")

            //THEN
            coVerify(exactly = 1) {
                profileRepository.updateProfile(
                    "animan",
                    "https://moyar.dev:8080/photo/"
                )
            }
            Assert.assertEquals("animan", results[1].displayName)
            Assert.assertEquals("https://moyar.dev:8080/photo/", results[1].imageUri)
            job.cancel()
        }

    @Test
    fun `onSaveButtonClicked should navigate to navigateToProfile when when displayName or imageUrl is NOT empty or null`() =
        runTest {
            //GIVEN
            classToTest.onViewCreated(editProfileNavigator)

            //WHEN
            classToTest.onSaveButtonClicked("animan", "https://moyar.dev:8080/photo/")

            //THEN
            verify(exactly = 1) {
                classToTest.editProfileNavigator?.navigateToProfile(
                    User(
                        displayName = "animan",
                        photoName = "https://moyar.dev:8080/photo/"
                    )
                )
            }
        }
}