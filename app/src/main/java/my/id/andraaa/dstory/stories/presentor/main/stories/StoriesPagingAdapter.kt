package my.id.andraaa.dstory.stories.presentor.main.stories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import my.id.andraaa.dstory.databinding.StoryItemBinding
import my.id.andraaa.dstory.stories.data.service.response.Story

class StoriesPagingAdapter(
    diffCallback: DiffUtil.ItemCallback<Story>,
) : PagingDataAdapter<Story, StoryViewHolder>(diffCallback) {
    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
        holder.show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }
}