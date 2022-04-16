package com.artworkspace.storyapp.ui.location

import androidx.paging.ExperimentalPagingApi
import com.artworkspace.storyapp.data.StoryRepository
import com.artworkspace.storyapp.data.remote.response.StoriesResponse
import com.artworkspace.storyapp.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LocationViewModelTest {

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var locationViewModel: LocationViewModel

    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()
    private val dummyToken = "AUTH_TOKEN"

    @Before
    fun setup() {
        locationViewModel = LocationViewModel(storyRepository)
    }

    @Test
    fun `Get story with location successfully - Flow`(): Unit = runTest {

        val expectedResponse = flowOf(Result.success(dummyStoriesResponse))

        `when`(locationViewModel.getAllStories(dummyToken)).thenReturn(expectedResponse)

        locationViewModel.getAllStories(dummyToken).collect { actualResponse ->

            Assert.assertTrue(actualResponse.isSuccess)
            Assert.assertFalse(actualResponse.isFailure)

            actualResponse.onSuccess { storiesResponse ->
                Assert.assertNotNull(storiesResponse)
                Assert.assertSame(storiesResponse, dummyStoriesResponse)
            }
        }

        Mockito.verify(storyRepository).getAllStoriesWithLocation(dummyToken)
    }

    @Test
    fun `Get story with location failed - Flow`(): Unit = runTest {

        val expectedResponse: Flow<Result<StoriesResponse>> =
            flowOf(Result.failure(Exception("Failed")))

        `when`(locationViewModel.getAllStories(dummyToken)).thenReturn(expectedResponse)

        locationViewModel.getAllStories(dummyToken).collect { actualResponse ->

            Assert.assertFalse(actualResponse.isSuccess)
            Assert.assertTrue(actualResponse.isFailure)

            actualResponse.onFailure {
                Assert.assertNotNull(it)
            }
        }

        Mockito.verify(storyRepository).getAllStoriesWithLocation(dummyToken)
    }
}