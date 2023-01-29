package my.id.andraaa.dstory.stories.presentor.story

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.R
import my.id.andraaa.dstory.databinding.ActivityStoryBinding
import my.id.andraaa.dstory.stories.domain.NetworkResource
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoryActivity : AppCompatActivity() {
    private val viewModel by viewModel<StoryViewModel>()
    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyId: String

    override fun onStart() {
        super.onStart()

        storyId = intent.getStringExtra(STORY_ID_EXTRA) ?: ""
        viewModel.dispatch(StoryAction.LoadStory(storyId))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryBinding.inflate(layoutInflater)
        setupUI()
        setContentView(binding.root)
    }

    private fun setupUI() {
        binding.contentError.buttonRetry.setOnClickListener {
            viewModel.dispatch(StoryAction.LoadStory(storyId))
        }

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.state.collectLatest { state ->
                    when (state.story) {
                        is NetworkResource.Loaded -> {
                            val story = state.story.data
                            if (story != null) {
                                binding.contentStory.root.isVisible = true
                                binding.contentError.root.isVisible = false

                                if (story.photoUrl != null) {
                                    binding.contentStory.imageView.load(story.photoUrl)
                                    binding.contentStory.imageView.isVisible = true
                                }
                                binding.contentStory.textViewAuthor.text = "By ${story.name}"
                                binding.contentStory.textViewDescription.text = story.description
                            } else {
                                binding.contentStory.root.isVisible = false
                                binding.contentError.root.isVisible = true
                                binding.contentError.textViewError.text =
                                    this@StoryActivity.getString(
                                        R.string.story_not_found
                                    )
                            }
                            binding.contentStoryPlaceholder.root.isVisible = false
                        }
                        is NetworkResource.Error -> {
                            binding.contentStory.root.isVisible = false
                            binding.contentError.root.isVisible = true
                            binding.contentError.textViewError.text =
                                "Error: ${state.story.error.message}"
                            binding.contentStoryPlaceholder.root.isVisible = false
                        }
                        is NetworkResource.Loading -> {
                            binding.contentStory.root.isVisible = false
                            binding.contentError.root.isVisible = false
                            binding.contentStoryPlaceholder.root.isVisible = true
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val STORY_ID_EXTRA = "story_id"
    }
}