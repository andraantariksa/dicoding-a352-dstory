package my.id.andraaa.dstory

import androidx.paging.PagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import my.id.andraaa.dstory.stories.data.DicodingStoryDataSourceImpl
import my.id.andraaa.dstory.stories.data.service.DicodingStoryService
import my.id.andraaa.dstory.stories.data.service.response.StoriesResponse
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.presentor.main.stories.StoriesPagingSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class StoriesPagingSourceTest {
    @Test
    fun fetchFirstPage_isCorrect() = runTest {
        val storyFactory = StoryFactory()

        val page1 = List(5) {
            storyFactory.createStory()
        }

        val dicodingStoryServiceMock = mock<DicodingStoryService> {
            onBlocking { getStories(1) } doReturn StoriesResponse(
                error = false, message = "", listStory = page1
            )
        }
        val pagingSource =
            StoriesPagingSource(DicodingStoryDataSourceImpl(dicodingStoryServiceMock))

        val firstPageResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1, loadSize = 0, placeholdersEnabled = false
            )
        ) as? PagingSource.LoadResult.Page<Int, Story>
        assertNotNull(firstPageResult)
        assertEquals(page1, firstPageResult?.data)
        verify(dicodingStoryServiceMock).getStories(1)
    }

    @Test
    fun fetchLastPage_isCorrect() = runTest {
        val dicodingStoryServiceMock = mock<DicodingStoryService> {
            onBlocking { getStories(4) } doReturn StoriesResponse(
                error = false, message = "", listStory = listOf()
            )
        }
        val pagingSource =
            StoriesPagingSource(DicodingStoryDataSourceImpl(dicodingStoryServiceMock))

        val lastPageResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 4, loadSize = 0, placeholdersEnabled = false
            )
        ) as? PagingSource.LoadResult.Page<Int, Story>
        assertNotNull(lastPageResult)
        assertEquals(listOf<Story>(), lastPageResult?.data)
        verify(dicodingStoryServiceMock).getStories(4)
    }
}