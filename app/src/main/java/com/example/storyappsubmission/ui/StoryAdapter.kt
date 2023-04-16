package com.example.storyappsubmission.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyappsubmission.data.remote.response.ListStoryItem
import com.example.storyappsubmission.databinding.StoryRowBinding

class StoryAdapter(private val storyItem: List<ListStoryItem>) :
    RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = StoryRowBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = storyItem.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val (photoUrl, createdAt, name) = storyItem[position]
        viewHolder.tvCreatedAt.text = createdAt
        viewHolder.tvStoryTitle.text = name
        Glide.with(viewHolder.itemView.context)
            .load(photoUrl)
            .into(viewHolder.imgPhoto)
        viewHolder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(storyItem[position]) }
    }

    class ViewHolder(binding: StoryRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvCreatedAt: TextView = binding.createdAt
        val imgPhoto: ImageView = binding.imgUserPhotos
        val tvStoryTitle: TextView = binding.storyTitle
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }
}
