package com.artworkspace.storyapp.ui.splash

import androidx.lifecycle.ViewModel
import com.artworkspace.storyapp.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {

    /**
     * Get user's authentication token from data source
     *
     * @return Flow
     */
    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()
}