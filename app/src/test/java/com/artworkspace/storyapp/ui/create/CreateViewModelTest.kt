package com.artworkspace.storyapp.ui.create

import com.artworkspace.storyapp.data.AuthRepository
import com.artworkspace.storyapp.data.StoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CreateViewModelTest {

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var createViewModel: CreateViewModel

    private val dummyToken = "authentication_token"

    @Before
    fun setup() {
        createViewModel = CreateViewModel(authRepository, storyRepository)
    }

    @Test
    fun `Get authentication token successfully`() = runTest {
        val expectedToken = flowOf(dummyToken)

        `when`(createViewModel.getAuthToken()).thenReturn(expectedToken)

        createViewModel.getAuthToken().collect { actualToken ->
            Assert.assertNotNull(actualToken)
            Assert.assertEquals(dummyToken, actualToken)
        }

        Mockito.verify(authRepository).getAuthToken()
        Mockito.verifyNoInteractions(storyRepository)
    }

    @Test
    fun `Get authentication token successfully but null`() = runTest {
        val expectedToken = flowOf(null)

        `when`(createViewModel.getAuthToken()).thenReturn(expectedToken)

        createViewModel.getAuthToken().collect { actualToken ->
            Assert.assertNull(actualToken)
        }

        Mockito.verify(authRepository).getAuthToken()
        Mockito.verifyNoInteractions(storyRepository)
    }
}