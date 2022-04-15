package com.artworkspace.storyapp.ui.home.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artworkspace.storyapp.R
import com.artworkspace.storyapp.adapter.StoryListAdapter
import com.artworkspace.storyapp.data.remote.response.Story
import com.artworkspace.storyapp.databinding.FragmentHomeBinding
import com.artworkspace.storyapp.ui.create.CreateStoryActivity
import com.artworkspace.storyapp.ui.home.HomeActivity
import com.artworkspace.storyapp.utils.animateVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: StoryListAdapter

    private var token: String = ""
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FIXME: Try to using fragment arguments instead of this
        token = activity?.intent?.getStringExtra(HomeActivity.EXTRA_TOKEN)!!

        setSwipeRefreshLayout()
        setRecyclerView()
        getAllStories()

        binding.fabCreateStory.setOnClickListener {
            Intent(requireContext(), CreateStoryActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Get all stories data and set the related views state
     */
    private fun getAllStories() {
        binding.viewLoading.animateVisibility(true)
        binding.swipeRefresh.isRefreshing = true

        lifecycleScope.launch {
            // Always repeat in the onResume state
            // To make sure the recyclerview always get the latest data
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {

                homeViewModel.getAllStories(token).collect { result ->
                    result.onSuccess { response ->
                        updateRecyclerViewData(response.stories)

                        binding.apply {
                            tvNotFoundError.animateVisibility(response.stories.isEmpty())
                            ivNotFoundError.animateVisibility(response.stories.isEmpty())
                            rvStories.animateVisibility(response.stories.isNotEmpty())
                            viewLoading.animateVisibility(false)

                            swipeRefresh.isRefreshing = false
                            rvStories.visibility =
                                if (response.stories.isEmpty()) View.GONE else View.VISIBLE
                        }
                    }

                    result.onFailure {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.error_occurred_message),
                            Toast.LENGTH_SHORT
                        ).show()

                        binding.apply {
                            tvNotFoundError.animateVisibility(true)
                            ivNotFoundError.animateVisibility(true)
                            rvStories.animateVisibility(false)
                            viewLoading.animateVisibility(false)
                            swipeRefresh.isRefreshing = false
                        }
                    }
                }
            }
        }
    }

    /**
     * Set the SwipeRefreshLayout state
     */
    private fun setSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            getAllStories()
            binding.viewLoading.animateVisibility(false)
        }
    }

    /**
     * Set the RecyclerView UI state
     *
     */
    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        listAdapter = StoryListAdapter()

        recyclerView = binding.rvStories
        recyclerView.apply {
            adapter = listAdapter
            layoutManager = linearLayoutManager
        }
    }

    /**
     * Update RecyclerView adapter data
     *
     * @param stories New data
     */
    private fun updateRecyclerViewData(stories: List<Story>) {
        // SaveInstanceState of recyclerview before add new data
        // It's prevent the recyclerview to scroll again to the top when load new data
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()

        // Add data to the adapter
        listAdapter.submitList(stories)

        // Restore last recyclerview state
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }
}