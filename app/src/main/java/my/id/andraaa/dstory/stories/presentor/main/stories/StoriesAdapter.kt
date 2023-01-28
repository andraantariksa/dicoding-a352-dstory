package my.id.andraaa.dstory.stories.presentor.main.stories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import my.id.andraaa.dstory.databinding.StoryItemBinding
import my.id.andraaa.dstory.databinding.StoryPlaceholderItemBinding
import my.id.andraaa.dstory.stories.data.service.response.Story

open class StoriesAdapter(val stories: List<Story>) :
    RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = stories[position]
        holder.binding.apply {
            imageView.load(story.photoUrl)
            textViewDescription.text = story.description
        }
    }

    override fun getItemCount(): Int = stories.size

    class ViewHolder(val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root)
}


class StoriesLoadingAdapter : RecyclerView.Adapter<StoriesLoadingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            StoryPlaceholderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = 10

    class ViewHolder(binding: StoryPlaceholderItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}