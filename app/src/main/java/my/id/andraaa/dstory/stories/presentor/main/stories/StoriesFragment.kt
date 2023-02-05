package my.id.andraaa.dstory.stories.presentor.main.stories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import my.id.andraaa.dstory.databinding.FragmentStoriesBinding
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.presentor.add_story.AddStoryBottomSheet
import my.id.andraaa.dstory.stories.presentor.common.SpaceItemDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoriesFragment : Fragment() {
    private val viewModel by viewModel<StoriesViewModel>()

    private var _addStoryBottomSheet: AddStoryBottomSheet? = null
    private val addStoryBottomSheet: AddStoryBottomSheet
        get() = _addStoryBottomSheet!!
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
        _addStoryBottomSheet = null
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
        _addStoryBottomSheet = AddStoryBottomSheet().apply {
            onFinished = {
                pagingAdapter.refresh()
            }
        }

        binding.recyclerViewStories.adapter = pagingAdapter
        binding.recyclerViewStories.addItemDecoration(SpaceItemDecoration(4, 32))
        binding.floatingActionButton.setOnClickListener {
            if (!addStoryBottomSheet.isAdded) {
                addStoryBottomSheet.showNow(childFragmentManager, null)

            }
        }
        binding.errorContent.buttonRetry.setOnClickListener {
            pagingAdapter.retry()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.state.onEach { state ->
                state.stories.onEach {
                    pagingAdapter.submitData(it)
                }
                    .flowOn(Dispatchers.IO)
                    .launchIn(this@launchWhenResumed)

                pagingAdapter.loadStateFlow.onEach {
//                    val refreshState = it.refresh

                }
                    .flowOn(Dispatchers.Main)
                    .launchIn(this@launchWhenResumed)
            }.launchIn(this)
        }
    }
}