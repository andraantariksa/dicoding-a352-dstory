package my.id.andraaa.dstory

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.presentor.main.stories.StoriesPagingAdapter
import org.junit.Before
import org.junit.Test

//@ExperimentalCoroutinesApi
//class MainDispatcherRule(
//    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
//) : TestWatcher() {
//    override fun starting(description: Description) {
//        Dispatchers.setMain(testDispatcher)
//    }
//
//    override fun finished(description: Description) {
//        Dispatchers.resetMain()
//    }
//}

@OptIn(ExperimentalCoroutinesApi::class)
class StoriesViewModelTest {
    //    @get:Rule
//    val coroutineRule = MainDispatcherRule()
    @Before
    fun setUp() {
        StoriesPagingAdapter(Story.DIFF_UTIL)
    }

    @Test
    fun storiesFlow_isCorrect() = runTest {
//        val storyFactory = StoryFactory()
//
//        val page1 = List(5) {
//            storyFactory.createStory()
//        }
//        val page2 = List(5) {
//            storyFactory.createStory()
//        }
//
//        val dicodingStoryServiceMock = mock<DicodingStoryService> {
//            onBlocking { getStories(1) } doReturn StoriesResponse(
//                error = false,
//                message = "",
//                listStory = page1
//            )
//            onBlocking { getStories(2) } doReturn StoriesResponse(
//                error = false,
//                message = "",
//                listStory = page2
//            )
//            onBlocking { getStories(3) } doReturn StoriesResponse(
//                error = false, message = "", listStory = listOf()
//            )
//        }
//        val dicodingStoryDataSource = DicodingStoryDataSourceImpl(dicodingStoryServiceMock)
//        val pagingSource = StoriesPagingSource(dicodingStoryDataSource)
//        val viewModel = StoriesViewModel(dicodingStoryDataSource)
//
//        val stories = viewModel.storiesFlow.first()

//        val adapter = StoriesPagingAdapter(
//            Story.DIFF_UTIL
//        )
//        adapter.submitData(stories)
//        pagingSource.
//        assertEquals(page1, adapter.snapshot().items)
    }
}