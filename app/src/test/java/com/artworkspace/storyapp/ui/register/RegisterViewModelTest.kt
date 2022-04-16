package com.artworkspace.storyapp.ui.register

import com.artworkspace.storyapp.data.AuthRepository
import com.artworkspace.storyapp.data.remote.response.RegisterResponse
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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var registerViewModel: RegisterViewModel

    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyName = "Full Name"
    private val dummyEmail = "email@mail.com"
    private val dummyPassword = "password"

    @Before
    fun setup() {
        registerViewModel = RegisterViewModel(authRepository)
    }

    @Test
    fun `Registration successfully - Flow`(): Unit = runTest {
        val expectedResponse = flowOf(Result.success(dummyRegisterResponse))

        `when`(registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )

        registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword).collect { response ->

            Assert.assertTrue(response.isSuccess)
            Assert.assertFalse(response.isFailure)

            response.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertSame(dummyRegisterResponse, actualResponse)
            }
        }

        Mockito.verify(authRepository).userRegister(dummyName, dummyEmail, dummyPassword)
    }

    @Test
    fun `Registration failed - Flow`(): Unit = runTest {
        val expectedResponse: Flow<Result<RegisterResponse>> =
            flowOf(Result.failure(Exception("failed")))

        `when`(registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )

        registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword).collect { response ->

            Assert.assertFalse(response.isSuccess)
            Assert.assertTrue(response.isFailure)

            response.onFailure {
                Assert.assertNotNull(it)
            }
        }
    }

}