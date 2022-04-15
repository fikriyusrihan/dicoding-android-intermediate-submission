package com.artworkspace.storyapp.ui.home.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.artworkspace.storyapp.data.AuthRepository
import com.artworkspace.storyapp.data.StoryRepository
import com.artworkspace.storyapp.data.remote.response.StoriesResponse
import com.artworkspace.storyapp.data.remote.response.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getStory(token: String): Flow<PagingData<Story>> =
        storyRepository.getStory(token).cachedIn(viewModelScope)

    /**
     * Get all user stories from data source
     *
     * @param token User's authentication token
     * @return Flow
     */
    suspend fun getAllStories(token: String): Flow<Result<StoriesResponse>> =
        storyRepository.getAllStories(token, null, null)
}