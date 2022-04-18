package com.artworkspace.storyapp.ui.setting

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.artworkspace.storyapp.R
import com.artworkspace.storyapp.databinding.FragmentSettingsBinding
import com.artworkspace.storyapp.ui.auth.AuthActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingViewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnLanguageSetting.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            btnLogout.setOnClickListener {
                showLogoutDialog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Show logout alert dialog before user logout
     */
    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.logout_dialog_title))
            .setMessage(getString(R.string.logout_dialog_message))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.logout)) { _, _ ->
                settingViewModel.saveAuthToken("")
                Intent(requireContext(), AuthActivity::class.java).also { intent ->
                    startActivity(intent)
                    requireActivity().finish()
                }
                Toast.makeText(
                    requireContext(),
                    getString(R.string.logout_message_success),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            .show()
    }
}