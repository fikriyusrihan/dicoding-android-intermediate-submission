package com.artworkspace.storyapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.artworkspace.storyapp.data.remote.response.FileUploadResponse
import com.artworkspace.storyapp.data.remote.response.StoriesResponse
import com.artworkspace.storyapp.data.remote.response.Story
import com.artworkspace.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class StoryRepository @Inject constructor(
    private val apiService: ApiService
) {

    /**
     * Provide all stories data from the data source
     *
     * @param token User's authentication token
     * @return Flow
     */
    fun getAllStories(token: String): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, generateBearerToken(token))
            }
        ).flow
    }

    /**
     * Provide latest story with its location
     *
     * @param token User's authentication token
     * @return Flow
     */
    fun getAllStoriesWithLocation(token: String): Flow<Result<StoriesResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.getAllStories(bearerToken, size = 30, location = 1)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    /**
     * Handle image uploading process to the server
     *
     * @param token User's authentication token
     * @param file Image file
     * @param description Image description
     * @param lat Latitude
     * @param lon Longitude
     */
    suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): Flow<Result<FileUploadResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.uploadImage(bearerToken, file, description, lat, lon)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

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