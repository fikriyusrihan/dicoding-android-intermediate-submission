package com.artworkspace.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.artworkspace.storyapp.data.local.AuthPreferencesDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "application")

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    /**
     * Provide datastore instance for Hilt
     *
     * @param context Application context
     * @return DataStore
     */
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    /**
     * Provide `AuthPreferencesDataSource` instance for Hilt
     *
     * @param dataStore DataStore
     * @return AuthPreferencesDataSource
     */
    @Provides
    @Singleton
    fun provideAuthPreferences(dataStore: DataStore<Preferences>): AuthPreferencesDataSource =
        AuthPreferencesDataSource(dataStore)
}