package com.artworkspace.storyapp.ui.login

import androidx.lifecycle.ViewModel
import com.artworkspace.storyapp.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * Do authentication for the users
     *
     * @param email User's email
     * @param password User's password
     * @return Flow
     */
    suspend fun userLogin(email: String, password: String) =
        authRepository.userLogin(email, password)
}