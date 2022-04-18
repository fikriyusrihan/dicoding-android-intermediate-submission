package com.artworkspace.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artworkspace.storyapp.adapter.LoadingStateAdapter
import com.artworkspace.storyapp.adapter.StoryListAdapter
import com.artworkspace.storyapp.data.local.entity.Story
import com.artworkspace.storyapp.databinding.FragmentHomeBinding
import com.artworkspace.storyapp.ui.create.CreateStoryActivity
import com.artworkspace.storyapp.ui.main.MainActivity
import com.artworkspace.storyapp.utils.animateVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalPagingApi
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
        // Using layoutInflater from activity
        // So, the SharedElement transition can works
        _binding = FragmentHomeBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = requireActivity().intent.getStringExtra(MainActivity.EXTRA_TOKEN) ?: ""

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
        homeViewModel.getAllStories(token).observe(viewLifecycleOwner) { result ->
            updateRecyclerViewData(result)
        }
    }

    /**
     * Set the SwipeRefreshLayout state
     */
    private fun setSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            getAllStories()
        }
    }

    /**
     * Set the RecyclerView UI state
     *
     */
    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        listAdapter = StoryListAdapter()

        // Pager LoadState listener
        listAdapter.addLoadStateListener { loadState ->
            if ((loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && listAdapter.itemCount < 1) || loadState.source.refresh is LoadState.Error) {
                // List empty or error
                binding.apply {
                    tvNotFoundError.animateVisibility(true)
                    ivNotFoundError.animateVisibility(true)
                    rvStories.animateVisibility(false)
                }
            } else {
                // List not empty
                binding.apply {
                    tvNotFoundError.animateVisibility(false)
                    ivNotFoundError.animateVisibility(false)
                    rvStories.animateVisibility(true)
                }
            }

            // SwipeRefresh status based on LoadState
            binding.swipeRefresh.isRefreshing = loadState.source.refresh is LoadState.Loading
        }

        recyclerView = binding.rvStories
        recyclerView.apply {
            adapter = listAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    listAdapter.retry()
                }
            )
            layoutManager = linearLayoutManager
        }
    }

    /**
     * Update RecyclerView adapter data
     *
     * @param stories New data
     */
    private fun updateRecyclerViewData(stories: PagingData<Story>) {
        // SaveInstanceState of recyclerview before add new data
        // It's prevent the recyclerview to scroll again to the top when load new data
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()

        // Add data to the adapter
        listAdapter.submitData(lifecycle, stories)

        // Restore last recyclerview state
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }
}