package com.gathering.android.profile

import com.gathering.android.MainDispatcherRule
import com.gathering.android.auth.model.User
import com.gathering.android.common.TokenRepository
import com.gathering.android.common.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    private lateinit var userRepository: UserRepository

    @MockK(relaxed = true)
    private lateinit var tokenRepository: TokenRepository

    @MockK(relaxed = true)
    private lateinit var profileNavigator: ProfileNavigator

    private lateinit var classToTest: ProfileViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        classToTest = ProfileViewModel(userRepository, tokenRepository)
    }

    @Test
    fun `onViewCreated sets navigator properly`() {

        //WHEN
        classToTest.onViewCreated(profileNavigator)

        // THEN
        assertEquals(profileNavigator, classToTest.profileNavigator)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onViewCreated set state appropriately when the User is not null`() =
        runTest {
            // GIVEN
            coEvery { userRepository.getUser() } returns User(
                displayName = "Ani",
                email = "animansoubi@gmail.com"
            )
            val results = mutableListOf<ProfileUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onViewCreated(profileNavigator)
            runCurrent()

            //THEN
            assertEquals(results[0].imageUri, null)
            assertEquals(results[0].displayName,null)
            assertEquals(results[0].email, null)
            assertEquals(results[1].imageUri, "https://moyar.dev:8080/photo/")
            assertEquals(results[1].displayName, "Ani")
            assertEquals(results[1].email, "animansoubi@gmail.com")
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onViewCreated set state appropriately when get User API fails`() =
        runTest {
            // GIVEN
            val e = RuntimeException("random exception")
            coEvery { userRepository.getUser() }.throws(e)

            val results = mutableListOf<ProfileUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onViewCreated(profileNavigator)
            runCurrent()

            //THEN
            assertEquals(results[0].imageUri, null)
            assertEquals(results[0].displayName, null)
            assertEquals(results[0].email, null)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onUserProfileUpdated set state appropriately when the User is not null`() =
        runTest {
            // GIVEN
            coEvery { userRepository.getUser() } returns User(
                displayName = "Ani",
                email = "animansoubi@gmail.com"
            )
            val results = mutableListOf<ProfileUiState>()
            val job = launch(UnconfinedTestDispatcher(testScheduler)) {
                classToTest.uiState.toList(results)
            }

            //WHEN
            classToTest.onViewCreated(profileNavigator)
            runCurrent()

            //THEN
            assertEquals(results[0].imageUri, null)
            assertEquals(results[0].displayName, null)
            assertEquals(results[0].email, null)
            assertEquals(results[1].displayName, "Ani")
            assertEquals(results[1].imageUri, "https://moyar.dev:8080/photo/")
            assertEquals(results[1].email, "animansoubi@gmail.com")
            job.cancel()
        }

    @Test
    fun `onFavoriteEventLayoutClicked should navigate to FavoriteEvent`() {
        // GIVEN
        classToTest.onViewCreated(profileNavigator)

        //WHEN
        classToTest.onFavoriteEventLayoutClicked()

        //THEN
        verify(exactly = 1) { profileNavigator.navigateToFavoriteEvent() }
    }

    @Test
    fun `onPersonalDataLayoutClicked should navigate to edit profile`() {
        // GIVEN
        classToTest.onViewCreated(profileNavigator)

        //WHEN
        classToTest.onPersonalDataLayoutClicked()

        //THEN
        verify(exactly = 1) { profileNavigator.navigateToEditProfile() }
    }

    @Test
    fun `onSignOutButtonClicked should clear User Info and navigate to intro`() = runTest {
        // GIVEN
        classToTest.onViewCreated(profileNavigator)

        //WHEN
        classToTest.onSignOutButtonClicked()

        //THEN
        verify(exactly = 1) { userRepository.clearUser() }
        verify(exactly = 1) { tokenRepository.clearToken() }
        verify(exactly = 1) { profileNavigator.navigateToIntro() }
    }
}
