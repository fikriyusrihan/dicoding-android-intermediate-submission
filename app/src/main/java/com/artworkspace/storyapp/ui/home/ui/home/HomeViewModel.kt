package com.artworkspace.storyapp.ui.home.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artworkspace.storyapp.data.AuthRepository
import com.artworkspace.storyapp.data.StoryRepository
import com.artworkspace.storyapp.data.remote.response.StoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    /**
     * Save user's authentication token
     *
     * @param token User's authentication token
     */
    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            authRepository.saveAuthToken(token)
        }
    }

    /**
     * Get all user stories from data source
     *
     * @param token User's authentication token
     * @return Flow
     */
    suspend fun getAllStories(token: String): Flow<Result<StoriesResponse>> =
        storyRepository.getAllStories(token, null, null)
}