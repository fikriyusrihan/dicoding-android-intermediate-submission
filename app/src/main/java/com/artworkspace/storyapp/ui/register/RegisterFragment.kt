package com.artworkspace.storyapp.ui.register

import android.animation.ObjectAnimator
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
import com.artworkspace.storyapp.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var registerJob: Job = Job()
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_loginFragment)
        )


        binding.btnRegister.setOnClickListener {
            handleRegister()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Handle register process for users
     */
    private fun handleRegister() {
        val name = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        setLoadingState(true)

        lifecycleScope.launchWhenResumed {
            // Make sure only one job that handle the registration process
            if (registerJob.isActive) registerJob.cancel()

            registerJob = launch {
                viewModel.userRegister(name, email, password).collect { result ->
                    result.onSuccess {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.registration_success),
                            Toast.LENGTH_SHORT
                        ).show()

                        Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_loginFragment)
                        setLoadingState(false)
                    }

                    result.onFailure {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.registration_error_message),
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
            etFullName.isEnabled = !isLoading
            btnLogin.isEnabled = !isLoading

            if (isLoading) {
                ObjectAnimator.ofFloat(viewLoading, View.ALPHA, 0.4f).setDuration(400).start()
                ObjectAnimator.ofFloat(pbLoading, View.ALPHA, 1f).setDuration(400).start()
            } else {
                ObjectAnimator.ofFloat(viewLoading, View.ALPHA, 0f).setDuration(400).start()
                ObjectAnimator.ofFloat(pbLoading, View.ALPHA, 0f).setDuration(400).start()
            }
        }
    }

    companion object {
        private const val TAG = "RegisterFragment"
    }
}