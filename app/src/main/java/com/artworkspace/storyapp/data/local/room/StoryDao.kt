package com.artworkspace.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.artworkspace.storyapp.data.local.entity.Story

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(vararg story: Story)

    @Query("SELECT * FROM story")
    fun getAllStories(): PagingSource<Int, Story>

    @Query("DELETE FROM story")
    fun deleteAll()
}