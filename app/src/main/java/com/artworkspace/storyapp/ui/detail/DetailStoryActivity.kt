package com.artworkspace.storyapp.ui.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.artworkspace.storyapp.R
import com.artworkspace.storyapp.data.local.entity.Story
import com.artworkspace.storyapp.databinding.ActivityDetailStoryBinding
import com.artworkspace.storyapp.utils.setLocalDateFormat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportPostponeEnterTransition()

        val story = intent.getParcelableExtra<Story>(EXTRA_DETAIL)
        parseStoryData(story)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Parse story data to related views
     *
     * @param storyResponseItem Story data to parse
     */
    private fun parseStoryData(storyResponseItem: Story?) {
        if (storyResponseItem != null) {
            binding.apply {
                tvStoryUsername.text = storyResponseItem.name
                tvStoryDescription.text = storyResponseItem.description
                toolbar.title = getString(R.string.detail_toolbar_title, storyResponseItem.name)
                tvStoryDate.setLocalDateFormat(storyResponseItem.createdAt)

                // Parse image to ImageView
                // Using listener for make sure the enter transition only called when loading completed
                Glide
                    .with(this@DetailStoryActivity)
                    .load(storyResponseItem.photoUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            supportStartPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            supportStartPostponedEnterTransition()
                            return false
                        }
                    })
                    .placeholder(R.drawable.image_loading_placeholder)
                    .error(R.drawable.image_load_error)
                    .into(ivStoryImage)
            }
        }
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
}