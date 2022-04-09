package com.artworkspace.storyapp.data

import com.artworkspace.storyapp.data.remote.response.StoriesResponse
import com.artworkspace.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class StoryRepository @Inject constructor(
    private val apiService: ApiService
) {

    /**
     * Provide all stories data from the data source
     *
     * @param token User's authentication token
     * @param page
     * @param size
     * @return Flow
     */
    suspend fun getAllStories(
        token: String,
        page: Int?,
        size: Int?
    ): Flow<Result<StoriesResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.getAllStories(bearerToken, page, size)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Give `Bearer` prefix to the given user's token
     *
     * @param token User's authentication token
     * @return Bearer token
     */
    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }
}