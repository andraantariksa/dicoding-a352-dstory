package my.id.andraaa.dstory.stories.presentor.main.stories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import my.id.andraaa.dstory.stories.data.DicodingStoryDataSource
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.util.MVIViewModel

data class StoriesState(
    val storiesPager: Pager<Int, Story>,
) {
    val storiesFlow
        get() = storiesPager.flow
}

object StoriesSideEffect

sealed class StoriesAction {
}

class StoriesViewModel(private val dicodingStoryDataSource: DicodingStoryDataSource) :
    MVIViewModel<StoriesState, StoriesAction, StoriesSideEffect>(
        StoriesState(
            storiesPager = Pager(
                config = PagingConfig(pageSize = 10),
                pagingSourceFactory = {
                    StoriesPagingSource(dicodingStoryDataSource)
                }),
        )
    )