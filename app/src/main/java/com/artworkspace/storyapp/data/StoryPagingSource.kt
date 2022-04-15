package com.artworkspace.storyapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.artworkspace.storyapp.data.remote.response.Story
import com.artworkspace.storyapp.data.remote.retrofit.ApiService

class StoryPagingSource(private val apiService: ApiService, private val token: String) :
    PagingSource<Int, Story>() {
    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllStories(token, position, params.loadSize)

            LoadResult.Page(
                data = responseData.stories,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.stories.isNullOrEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            Log.d(TAG, "load: error")
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        private const val TAG = "StoryPagingSource"
    }
}