package my.id.andraaa.dstory.stories.presentor.main.stories

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import my.id.andraaa.dstory.databinding.StoryItemBinding
import my.id.andraaa.dstory.databinding.StoryPlaceholderItemBinding
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.presentor.story.StoryActivity

open class StoriesAdapter(val stories: List<Story>) :
    RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = stories[position]
        holder.binding.contentStoryItem.apply {
            imageView.load(story.photoUrl)
            textViewDescription.text = story.description
            cardStory.setOnClickListener {
                val context = root.context
                val intent = Intent(context, StoryActivity::class.java).apply {
                    putExtra(StoryActivity.STORY_ID_EXTRA, story.id)
                }
                context.startActivity(intent)
            }
        }
        holder.show(position.toLong())
    }

    override fun getItemCount(): Int = stories.size

    class ViewHolder(
        val binding: StoryItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun show(index: Long) {
            binding.contentStoryPlaceholderItem.root.apply {
                animate()
                    .alpha(0.0F)
                    .setStartDelay(START_FACTOR_DURATION * index)
                    .setDuration(
                        CROSSFADE_DURATION
                    )
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            isVisible = false
                        }
                    })
                    .start()
            }

            binding.contentStoryItem.root.apply {
                alpha = 0.0F
                animate()
                    .alpha(1.0F)
                    .setStartDelay(START_FACTOR_DURATION * index)
                    .setDuration(
                        CROSSFADE_DURATION
                    )
                    .start()
            }
        }

        companion object {
            const val START_FACTOR_DURATION = 300L
            const val CROSSFADE_DURATION = 1000L
        }
    }
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