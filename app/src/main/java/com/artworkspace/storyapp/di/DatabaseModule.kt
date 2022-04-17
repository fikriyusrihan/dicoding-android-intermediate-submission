package com.artworkspace.storyapp.di

import android.content.Context
import androidx.room.Room
import com.artworkspace.storyapp.data.local.room.RemoteKeysDao
import com.artworkspace.storyapp.data.local.room.StoryDao
import com.artworkspace.storyapp.data.local.room.StoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    /**
     * Provide StoryDao instance for Hilt
     *
     * @param storyDatabase Local StoryDatabase
     */
    @Provides
    fun provideStoryDao(storyDatabase: StoryDatabase): StoryDao = storyDatabase.storyDao()

    /**
     * Provide RemoteKeysDao instance for Hilt
     *
     * @param storyDatabase Local StoryDatabase
     */
    @Provides
    fun provideRemoteKeysDao(storyDatabase: StoryDatabase): RemoteKeysDao =
        storyDatabase.remoteKeysDao()


    /**
     * Provide StoryDatabase instance for Hilt
     *
     * @param context Context
     */
    @Provides
    @Singleton
    fun provideStoryDatabase(@ApplicationContext context: Context): StoryDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            StoryDatabase::class.java,
            "story_database"
        ).build()
    }
}