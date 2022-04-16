package com.artworkspace.storyapp.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import com.artworkspace.storyapp.data.StoryRepository
import com.artworkspace.storyapp.data.remote.response.StoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class LocationViewModel @Inject constructor(private val storyRepository: StoryRepository) :
    ViewModel() {

    fun getAllStories(token: String): Flow<Result<StoriesResponse>> =
        storyRepository.getAllStoriesWithLocation(token)

    fun getStoriesWithLocation(token: String): LiveData<Result<StoriesResponse>> =
        storyRepository.getAllStoriesWithLocation(token).asLiveData()
}