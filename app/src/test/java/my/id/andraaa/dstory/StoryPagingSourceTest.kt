package my.id.andraaa.dstory

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import my.id.andraaa.dstory.stories.data.DicodingStoryDataSource
import my.id.andraaa.dstory.stories.data.DicodingStoryServiceMock
import my.id.andraaa.dstory.stories.data.service.DicodingStoryService
import org.junit.Before
import org.junit.Test

class StoriesPagingSourceTest {
    private lateinit var dicodingStoryService: DicodingStoryService
    private lateinit var dicodingStoryDataSource: DicodingStoryDataSource

    @Before
    fun setUp() {
        dicodingStoryService = DicodingStoryServiceMock()
        dicodingStoryDataSource = DicodingStoryDataSource(dicodingStoryService)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addition_isCorrect() = runTest {
        val pagingSource = StoriesPagingSource(dicodingStoryDataSource)
//        assertEquals(
//            expected = PagingSource.LoadResult.Page(
//                data = listOf(mockPosts[0], mockPosts[1]),
//                prevKey = null,
//                nextKey = 3
//            ),
//            actual = pagingSource.load(
//                PagingSource.LoadParams.Refresh(
//                    key = null,
//                    loadSize = 2,
//                    placeholdersEnabled = false
//                )
//            ),
//        )
    }
}