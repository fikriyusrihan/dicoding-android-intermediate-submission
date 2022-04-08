package com.artworkspace.storyapp.data

import com.artworkspace.storyapp.data.remote.response.LoginResponse
import com.artworkspace.storyapp.data.remote.response.RegisterResponse
import com.artworkspace.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.Result

class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {

    /**
     * Handle login operation for the users by calling the related API
     *
     * @param email User's email
     * @param password User's password
     * @return Flow
     */
    suspend fun userLogin(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.userLogin(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    /**
     * Handle registration process for the users by calling the related API
     *
     * @param name User's full name
     * @param email User's email
     * @param password User's password
     * @return Flow
     */
    suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = apiService.userRegister(name, email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }
}