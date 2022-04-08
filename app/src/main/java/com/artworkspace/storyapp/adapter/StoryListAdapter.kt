package com.artworkspace.storyapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artworkspace.storyapp.data.remote.response.Story
import com.artworkspace.storyapp.databinding.LayoutStoryItemBinding
import com.artworkspace.storyapp.utils.setImageFromUrl
import com.artworkspace.storyapp.utils.setLocalDateFormat


class StoryListAdapter(private val stories: List<Story>) :
    RecyclerView.Adapter<StoryListAdapter.ViewHolder>() {
    class ViewHolder(private val binding: LayoutStoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: Story) {
            binding.apply {
                tvStoryUsername.text = story.name
                tvStoryDescription.text = story.description
                ivStoryImage.setImageFromUrl(context, story.photoUrl)
                tvStoryDate.setLocalDateFormat(story.createdAt)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutStoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(holder.itemView.context, story)
    }

    override fun getItemCount(): Int = stories.size

}