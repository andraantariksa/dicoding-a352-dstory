package my.id.andraaa.dstory.stories.presentor.main.stories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import my.id.andraaa.dstory.stories.data.DicodingStoryDataSource
import my.id.andraaa.dstory.stories.data.service.response.Story

class StoriesPagingSource(
    private val dicodingStoryDataSource: DicodingStoryDataSource
) : PagingSource<Int, Story>() {
    override fun getRefreshKey(state: PagingState<Int, Story>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val currentPage = params.key ?: 1
        val stories = dicodingStoryDataSource.getStories(currentPage)
        return if (stories.isEmpty()) {
            LoadResult.Page(
                data = listOf(),
                prevKey = null,
                nextKey = null,
            )
        } else {
            LoadResult.Page(
                data = stories,
                prevKey = null,
                nextKey = currentPage + 1
            )
        }
    }
}