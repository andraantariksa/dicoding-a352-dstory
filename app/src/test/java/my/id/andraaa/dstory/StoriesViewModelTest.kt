package my.id.andraaa.dstory

import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import my.id.andraaa.dstory.stories.data.DicodingStoryDataSourceImpl
import my.id.andraaa.dstory.stories.data.service.DicodingStoryService
import my.id.andraaa.dstory.stories.data.service.response.StoriesResponse
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.presentor.main.stories.StoriesViewModel
import my.id.andraaa.dstory.utils.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class StoriesViewModelTest {
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    @Test
    fun storiesFlow_isCorrect() = runTest {
        val storyFactory = StoryFactory()

        val page1 = List(5) {
            storyFactory.createStory()
        }
        val page2 = List(5) {
            storyFactory.createStory()
        }

        val dicodingStoryServiceMock = mock<DicodingStoryService> {
            onBlocking { getStories(1) } doReturn StoriesResponse(
                error = false,
                message = "",
                listStory = page1
            )
            onBlocking { getStories(2) } doReturn StoriesResponse(
                error = false,
                message = "",
                listStory = page2
            )
            onBlocking { getStories(3) } doReturn StoriesResponse(
                error = false, message = "", listStory = listOf()
            )
        }
        val dicodingStoryDataSource = DicodingStoryDataSourceImpl(dicodingStoryServiceMock)
        val viewModel = StoriesViewModel(dicodingStoryDataSource)

        val differ = AsyncPagingDataDiffer(
            diffCallback = Story.DIFF_UTIL,
            updateCallback = object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) {}
                override fun onRemoved(position: Int, count: Int) {}
                override fun onMoved(fromPosition: Int, toPosition: Int) {}
                override fun onChanged(position: Int, count: Int, payload: Any?) {}
            },
            workerDispatcher = Dispatchers.Main
        )
        val job = viewModel.storiesFlow
            .onEach {
                differ.submitData(it)
            }
            .launchIn(this)

        advanceUntilIdle()

        assertNotNull(differ.snapshot().items)
        assertEquals(page1.size + page2.size, differ.snapshot().size)
        assertEquals(page1[0], differ.snapshot()[0])

        job.cancel()
    }

    @Test
    fun storiesFlow_isEmpty() = runTest {
        val dicodingStoryServiceMock = mock<DicodingStoryService> {
            onBlocking { getStories(1) } doReturn StoriesResponse(
                error = false, message = "", listStory = listOf()
            )
        }
        val dicodingStoryDataSource = DicodingStoryDataSourceImpl(dicodingStoryServiceMock)
        val viewModel = StoriesViewModel(dicodingStoryDataSource)

        val differ = AsyncPagingDataDiffer(
            diffCallback = Story.DIFF_UTIL,
            updateCallback = object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) {}
                override fun onRemoved(position: Int, count: Int) {}
                override fun onMoved(fromPosition: Int, toPosition: Int) {}
                override fun onChanged(position: Int, count: Int, payload: Any?) {}
            },
            workerDispatcher = Dispatchers.Main
        )
        val job = viewModel.storiesFlow
            .onEach {
                differ.submitData(it)
            }
            .launchIn(this)

        advanceUntilIdle()

        assertEquals(0, differ.snapshot().size)

        job.cancel()
    }
}