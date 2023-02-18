package my.id.andraaa.dstory.stories.presentor.main.stories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.domain.DicodingStoryDataSource

class StoriesPagingSource(
    private val dicodingStoryDataSource: DicodingStoryDataSource
) : PagingSource<Int, Story>() {
    override fun getRefreshKey(state: PagingState<Int, Story>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val currentPage = params.key ?: 1
        val prevKey = if (currentPage == 1) {
            null
        } else {
            currentPage - 1
        }
        val stories = dicodingStoryDataSource.getStories(currentPage)
        return if (stories.isEmpty()) {
            LoadResult.Page(
                data = listOf(),
                prevKey = prevKey,
                nextKey = null,
            )
        } else {
            LoadResult.Page(
                data = stories,
                prevKey = prevKey,
                nextKey = currentPage + 1
            )
        }
    }
}