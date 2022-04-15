package com.artworkspace.storyapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.artworkspace.storyapp.data.StoryRepository
import com.artworkspace.storyapp.data.remote.response.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    /**
     * Get all user stories from data source
     *
     * @param token User's authentication token
     * @return Flow
     */
    fun getAllStories(token: String): Flow<PagingData<Story>> =
        storyRepository.getAllStories(token).cachedIn(viewModelScope)
}