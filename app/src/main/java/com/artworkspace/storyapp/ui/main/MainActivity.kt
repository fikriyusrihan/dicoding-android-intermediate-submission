package com.artworkspace.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.artworkspace.storyapp.R
import com.artworkspace.storyapp.adapter.StoryListAdapter
import com.artworkspace.storyapp.data.remote.response.Story
import com.artworkspace.storyapp.databinding.ActivityMainBinding
import com.artworkspace.storyapp.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var token: String = ""
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        token = intent.getStringExtra(EXTRA_TOKEN)!!

        lifecycleScope.launchWhenStarted {
            viewModel.getAllStories(token).collect { result ->
                result.onSuccess { response ->
                    setRecyclerView(response.stories)
                }

                result.onFailure {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.error_occurred_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
     * Set the RecyclerView UI state
     *
     * @param stories List of story to display
     */
    private fun setRecyclerView(stories: List<Story>) {
        val recyclerView = binding.rvStories
        val adapter = StoryListAdapter(stories)
        val layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}