package com.artworkspace.storyapp.ui.create

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.artworkspace.storyapp.data.AuthRepository
import com.artworkspace.storyapp.data.StoryRepository
import com.artworkspace.storyapp.data.remote.response.FileUploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class CreateViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    /**
     * Get user's authentication token
     *
     * @return Flow
     */
    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

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
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Result<FileUploadResponse>> =
        storyRepository.uploadImage(token, file, description, lat, lon)
}