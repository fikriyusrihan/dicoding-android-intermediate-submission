package com.artworkspace.storyapp.ui.splash

import com.artworkspace.storyapp.data.AuthRepository
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
class SplashViewModelTest {


    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var splashViewModel: SplashViewModel

    private val dummyToken = "authentication_token"

    @Before
    fun setup() {
        splashViewModel = SplashViewModel(authRepository)
    }

    @Test
    fun `Get authentication token successfully`() = runTest {
        val expectedToken = flowOf(dummyToken)

        `when`(splashViewModel.getAuthToken()).thenReturn(expectedToken)

        splashViewModel.getAuthToken().collect { actualToken ->
            Assert.assertNotNull(actualToken)
            Assert.assertEquals(dummyToken, actualToken)
        }

        Mockito.verify(authRepository).getAuthToken()
    }

    @Test
    fun `Get authentication token empty`() = runTest {
        val expectedToken = flowOf(null)

        `when`(splashViewModel.getAuthToken()).thenReturn(expectedToken)

        splashViewModel.getAuthToken().collect { actualToken ->
            Assert.assertNull(actualToken)
        }

        Mockito.verify(authRepository).getAuthToken()
    }

}