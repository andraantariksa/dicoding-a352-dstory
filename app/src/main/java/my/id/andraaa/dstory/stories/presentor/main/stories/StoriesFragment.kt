package my.id.andraaa.dstory.stories.presentor.main.stories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.databinding.FragmentStoriesBinding
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.presentor.add_story.AddStoryBottomSheet
import my.id.andraaa.dstory.stories.presentor.common.SpaceItemDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoriesFragment : Fragment() {
    private val storiesViewModel by viewModel<StoriesViewModel>()
    private val addStoryBottomSheet = AddStoryBottomSheet()

    private lateinit var binding: FragmentStoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoriesBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() = lifecycleScope.launchWhenResumed {
        binding.floatingActionButton.setOnClickListener {
            addStoryBottomSheet.show(childFragmentManager, null)
        }

        launch {
            storiesViewModel.state.collectLatest { state ->
                when (state.stories) {
                    is NetworkResource.Loaded -> {
                        binding.imageViewEmpty.isVisible = state.stories.data.isEmpty()
                        binding.recyclerViewStories.adapter = StoriesAdapter(state.stories.data)
                        binding.recyclerViewStories.addItemDecoration(SpaceItemDecoration(4, 32))
                    }
                    is NetworkResource.Error -> {}
                    is NetworkResource.Loading -> {
                        binding.recyclerViewStories.adapter = StoriesLoadingAdapter()
                        binding.recyclerViewStories.addItemDecoration(SpaceItemDecoration(4, 32))
                    }
                }

                if (state.stories !is NetworkResource.Loaded) {
                    binding.imageViewEmpty.isVisible = false
                }
            }
        }
    }
}