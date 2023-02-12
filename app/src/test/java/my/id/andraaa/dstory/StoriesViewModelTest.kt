package my.id.andraaa.dstory

import kotlinx.coroutines.ExperimentalCoroutinesApi
import my.id.andraaa.dstory.stories.data.DicodingStoryDataSourceImpl
import my.id.andraaa.dstory.stories.data.service.DicodingStoryService
import my.id.andraaa.dstory.stories.data.service.response.StoriesResponse
import my.id.andraaa.dstory.stories.presentor.main.stories.StoriesViewModel
import org.junit.Before
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class StoriesViewModelTest {
    private lateinit var viewModel: StoriesViewModel

    @Before
    fun setUp() {
        val storyFactory = StoryFactory()

        val dicodingStoryServiceMock = mock<DicodingStoryService> {
            for (page in 1..2) {
                onBlocking { getStories(page) } doReturn StoriesResponse(error = false,
                    message = "",
                    listStory = List(5) {
                        storyFactory.createStory()
                    })
            }
            onBlocking { getStories(3) } doReturn StoriesResponse(
                error = false, message = "", listStory = listOf()
            )
        }
        val dicodingStoryDataSource = DicodingStoryDataSourceImpl(dicodingStoryServiceMock)
        viewModel = StoriesViewModel(dicodingStoryDataSource)
    }
}