package com.gathering.android.profile.favoriteEvent

import com.gathering.android.MainDispatcherRule
import com.gathering.android.event.repo.EventRepository
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoriteEventScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var sut: FavoriteEventScreenViewModel

    @MockK(relaxed = true)
    private lateinit var repository: EventRepository

    @MockK(relaxed = true)
    private lateinit var favoriteEventNavigator: FavoriteEventNavigator

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        sut = FavoriteEventScreenViewModel(
            eventRepository = repository
        )
    }

    @Test
    fun `onViewCreated sets navigator properly`() {
        //WHEN
        sut.onViewCreated(favoriteEventNavigator)

        // THEN
        Assert.assertEquals(favoriteEventNavigator, sut.favoriteEventNavigator)
    }
    
    @Test
    fun onNextPageRequested() {
    }

    @Test
    fun onEventItemClicked() {
    }
}