package com.artworkspace.storyapp.ui.register

import androidx.lifecycle.ViewModel
import com.artworkspace.storyapp.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * Handle registration process for the users
     *
     * @param name User's name
     * @param email User's email
     * @param password User's password
     * @return Flow
     */
    suspend fun userRegister(name: String, email: String, password: String) =
        authRepository.userRegister(name, email, password)
}