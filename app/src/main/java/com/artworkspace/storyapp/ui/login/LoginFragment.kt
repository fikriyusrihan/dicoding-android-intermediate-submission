package com.artworkspace.storyapp.ui.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.artworkspace.storyapp.R
import com.artworkspace.storyapp.databinding.FragmentLoginBinding
import com.artworkspace.storyapp.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var loginJob: Job = Job()
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Set actions listener on the UI
     */
    private fun setActions() {
        binding.apply {
            btnRegister.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment)
            )

            btnLogin.setOnClickListener {
                handleLogin()
            }
        }
    }

    /**
     * Handle login process for users
     */
    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        setLoadingState(true)

        lifecycleScope.launchWhenResumed {
            // Make sure only one job that handle the login process
            if (loginJob.isActive) loginJob.cancel()

            loginJob = launch {
                viewModel.userLogin(email, password).collect { result ->
                    result.onSuccess {
                        Intent(requireContext(), MainActivity::class.java).also { intent ->
                            startActivity(intent)
                            requireActivity().finish()
                        }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.login_success_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    result.onFailure {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.login_error_message),
                            Snackbar.LENGTH_SHORT
                        ).show()

                        setLoadingState(false)
                    }
                }
            }
        }

    }

    /**
     * Set related views state based on the loading value
     *
     * @param isLoading Loading state
     */
    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            etEmail.isEnabled = !isLoading
            etPassword.isEnabled = !isLoading
            btnLogin.isEnabled = !isLoading

            // Animate views alpha
            if (isLoading) {
                ObjectAnimator.ofFloat(viewLoading, View.ALPHA, 0.4f).setDuration(400).start()
                ObjectAnimator.ofFloat(pbLoading, View.ALPHA, 1f).setDuration(400).start()
            } else {
                ObjectAnimator.ofFloat(viewLoading, View.ALPHA, 0f).setDuration(400).start()
                ObjectAnimator.ofFloat(pbLoading, View.ALPHA, 0f).setDuration(400).start()
            }
        }
    }

}