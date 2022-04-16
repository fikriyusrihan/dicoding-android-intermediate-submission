package com.artworkspace.storyapp.data

import com.artworkspace.storyapp.CoroutinesTestRule
import com.artworkspace.storyapp.data.local.AuthPreferencesDataSource
import com.artworkspace.storyapp.data.remote.retrofit.ApiService
import com.artworkspace.storyapp.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var preferencesDataSource: AuthPreferencesDataSource
    private lateinit var apiService: ApiService
    private lateinit var authRepository: AuthRepository

    private val dummyName = "Name"
    private val dummyEmail = "mail@mail.com"
    private val dummyPassword = "password"
    private val dummyToken = "authentication_token"

    @Before
    fun setup() {
        apiService = FakeApiService()
        authRepository = AuthRepository(apiService, preferencesDataSource)
    }

    @Test
    fun `User login successfully`(): Unit = runTest {
        val expectedResponse = DataDummy.generateDummyLoginResponse()

        authRepository.userLogin(dummyEmail, dummyPassword).collect { result ->
            Assert.assertTrue(result.isSuccess)
            Assert.assertFalse(result.isFailure)

            result.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(expectedResponse, actualResponse)
            }

            result.onFailure {
                Assert.assertNull(it)
            }
        }

    }

    @Test
    fun `User register successfully`(): Unit = runTest {
        val expectedResponse = DataDummy.generateDummyRegisterResponse()

        authRepository.userRegister(dummyName, dummyEmail, dummyPassword).collect { result ->
            Assert.assertTrue(result.isSuccess)
            Assert.assertFalse(result.isFailure)

            result.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(expectedResponse, actualResponse)
            }

            result.onFailure {
                Assert.assertNull(it)
            }
        }
    }

    @Test
    fun `Save auth token successfully`() = runTest {
        authRepository.saveAuthToken(dummyToken)
        Mockito.verify(preferencesDataSource).saveAuthToken(dummyToken)
    }

    @Test
    fun `Get authentication token successfully`() = runTest {
        val expectedToken = flowOf(dummyToken)

        `when`(preferencesDataSource.getAuthToken()).thenReturn(expectedToken)

        authRepository.getAuthToken().collect { actualToken ->
            Assert.assertNotNull(actualToken)
            Assert.assertEquals(dummyToken, actualToken)
        }

        Mockito.verify(preferencesDataSource).getAuthToken()
    }

}