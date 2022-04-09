package com.artworkspace.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artworkspace.storyapp.R
import com.artworkspace.storyapp.adapter.StoryListAdapter
import com.artworkspace.storyapp.data.remote.response.Story
import com.artworkspace.storyapp.databinding.ActivityMainBinding
import com.artworkspace.storyapp.ui.auth.AuthActivity
import com.artworkspace.storyapp.ui.create.CreateStoryActivity
import com.artworkspace.storyapp.utils.animateVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: StoryListAdapter

    private var token: String = ""
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        token = intent.getStringExtra(EXTRA_TOKEN)!!

        setSwipeRefreshLayout()
        setRecyclerView()
        getAllStories()

        binding.fabCreateStory.setOnClickListener {
            Intent(this, CreateStoryActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                viewModel.saveAuthToken("")
                Intent(this, AuthActivity::class.java).also { intent ->
                    startActivity(intent)
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {

                viewModel.getAllStories(token).collect { result ->
                    result.onSuccess { response ->
                        updateRecyclerViewData(response.stories)

                        binding.apply {
                            tvNotFoundError.animateVisibility(response.stories.isEmpty())
                            ivNotFoundError.animateVisibility(response.stories.isEmpty())
                            rvStories.animateVisibility(response.stories.isNotEmpty())
                            viewLoading.animateVisibility(false)
                            swipeRefresh.isRefreshing = false
                        }
                    }

                    result.onFailure {
                        Toast.makeText(
                            this@MainActivity,
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
        val linearLayoutManager = LinearLayoutManager(this)
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

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}