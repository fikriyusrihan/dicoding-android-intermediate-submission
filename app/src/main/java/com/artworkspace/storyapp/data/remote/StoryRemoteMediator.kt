package com.artworkspace.storyapp.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.artworkspace.storyapp.data.local.entity.RemoteKeys
import com.artworkspace.storyapp.data.local.entity.Story
import com.artworkspace.storyapp.data.local.room.StoryDatabase
import com.artworkspace.storyapp.data.remote.retrofit.ApiService
import com.artworkspace.storyapp.utils.wrapEspressoIdlingResource

@ExperimentalPagingApi
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, Story>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {

        // Determine page value based on LoadType
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeysForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        wrapEspressoIdlingResource {
            try {
                val responseData = apiService.getAllStories(token, page, state.config.pageSize)
                val endOfPaginationReached = responseData.storyResponseItems.isEmpty()

                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.remoteKeysDao().deleteRemoteKeys()
                        database.storyDao().deleteAll()
                    }

                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = responseData.storyResponseItems.map {
                        RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }

                    // Save RemoteKeys information to database
                    database.remoteKeysDao().insertAll(keys)

                    // Convert StoryResponseItem class to Story class
                    // We need to convert because the response from API is different from local database Entity
                    responseData.storyResponseItems.forEach { storyResponseItem ->
                        val story = Story(
                            storyResponseItem.id,
                            storyResponseItem.name,
                            storyResponseItem.description,
                            storyResponseItem.createdAt,
                            storyResponseItem.photoUrl,
                            storyResponseItem.lon,
                            storyResponseItem.lat
                        )

                        // Save Story to the local database
                        database.storyDao().insertStory(story)
                    }
                }

                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

            } catch (e: Exception) {
                return MediatorResult.Error(e)
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    /**
     * Get RemoteKeys for last item from local database
     *
     * @param state PagingState
     */
    private suspend fun getRemoteKeysForLastItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    /**
     * Get RemoteKeys for first item from local database
     *
     * @param state PagingState
     */
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    /**
     * Get RemoteKeys for closest to current position from local database
     *
     * @param state PagingState
     */
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}