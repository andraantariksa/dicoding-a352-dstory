package my.id.andraaa.dstory.stories.presentor.main.stories

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import my.id.andraaa.dstory.databinding.StoryItemBinding
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.presentor.story.StoryActivity


class StoryViewHolder(
    val binding: StoryItemBinding,
) : RecyclerView.ViewHolder(binding.root) {
    private var seen = false

    fun bind(story: Story?) {
        if (story == null) {
            binding.contentStoryPlaceholderItem.root.isVisible = true
            binding.contentStoryItem.root.isVisible = false
            return
        }

        binding.contentStoryItem.apply {
            imageView.transitionName = "storyImage:${story.id}"
            imageView.load(story.photoUrl)
            textViewDescription.text = story.description
            textViewAuthor.text = story.name

            cardStory.setOnClickListener {
                val context = root.context
                val options = (context as? Activity)?.let {
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        it,
                        binding.contentStoryItem.imageView,
                        imageView.transitionName
                    )
                }
                val intent = Intent(context, StoryActivity::class.java).apply {
                    putExtra(StoryActivity.STORY_ID_EXTRA, story.id)
                }
                context.startActivity(intent, options?.toBundle())
            }
        }
    }

    fun show() {
        if (seen) return

        seen = true

        val duration = 700L
        binding.contentStoryPlaceholderItem.root.apply {
            animate().alpha(0.0F).setStartDelay(duration).setDuration(
                CROSSFADE_DURATION
            ).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isVisible = false
                }
            }).start()
        }

        binding.contentStoryItem.root.apply {
            alpha = 0.0F
            animate().alpha(1.0F).setStartDelay(duration).setDuration(
                CROSSFADE_DURATION
            ).start()
        }
    }

    companion object {
        const val CROSSFADE_DURATION = 100L
    }
}