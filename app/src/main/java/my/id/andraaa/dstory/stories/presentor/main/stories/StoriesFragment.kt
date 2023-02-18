package my.id.andraaa.dstory.stories.presentor.main.stories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.databinding.FragmentStoriesBinding
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.presentor.add_story.AddStoryBottomSheet
import my.id.andraaa.dstory.stories.presentor.common.SpaceItemDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoriesFragment : Fragment() {
    private val viewModel by viewModel<StoriesViewModel>()

    private var _pagingAdapter: StoriesPagingAdapter? = null
    private val pagingAdapter: StoriesPagingAdapter
        get() = _pagingAdapter!!

    private var _binding: FragmentStoriesBinding? = null
    private val binding: FragmentStoriesBinding
        get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()

        repeat(childFragmentManager.backStackEntryCount) {
            childFragmentManager.popBackStack()
        }
        _pagingAdapter = null
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoriesBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        _pagingAdapter = StoriesPagingAdapter(Story.DIFF_UTIL).apply {
            withLoadStateHeaderAndFooter(
                header = PagingLoadStateAdapter(this),
                footer = PagingLoadStateAdapter(this)
            )
        }

        binding.recyclerViewStories.adapter = pagingAdapter
        binding.recyclerViewStories.addItemDecoration(SpaceItemDecoration(4, 32))
        binding.errorContent.buttonRetry.setOnClickListener {
            pagingAdapter.retry()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            binding.floatingActionButton.setOnClickListener {
                if (childFragmentManager.backStackEntryCount == 0) {
                    val addStoryBottomSheet = AddStoryBottomSheet().apply {
                        onFinished = {
                            launch {
                                pagingAdapter.refresh()
                                pagingAdapter.onPagesUpdatedFlow.first()
                                binding.recyclerViewStories.layoutManager?.scrollToPosition(0)
                            }
                        }
                    }
                    addStoryBottomSheet.showNow(childFragmentManager, null)
                }
            }

            viewModel.storiesFlow
                .onEach {
                    pagingAdapter.submitData(it)
                }
                .flowOn(Dispatchers.IO)
                .launchIn(this)

//            viewModel.state.onEach { state ->
//                pagingAdapter.loadStateFlow.onEach {
//                    val refreshState = it.refresh
//
//                }
//                    .flowOn(Dispatchers.Main)
//                    .launchIn(this@launchWhenResumed)
//            }.launchIn(this)
        }
    }
}