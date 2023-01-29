package my.id.andraaa.dstory.stories.presentor.main.stories

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.stories.data.DicodingStoryDataSource
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.util.MVIViewModel

data class StoriesState(
    val stories: NetworkResource<List<Story>> = NetworkResource.Loading(),
)

object StoriesSideEffect

sealed class StoriesAction {
    object LoadStories : StoriesAction()
}

class StoriesViewModel(private val dicodingStoryDataSource: DicodingStoryDataSource) :
    MVIViewModel<StoriesState, StoriesAction, StoriesSideEffect>(StoriesState()) {

    init {
        dispatch(StoriesAction.LoadStories)
    }

    override fun reducer(state: StoriesState, action: StoriesAction): StoriesState {
        return when (action) {
            StoriesAction.LoadStories -> {
                viewModelScope.launch {
                    try {
                        _state.value = state.copy(stories = NetworkResource.Loaded(loadStories()))
                    } catch (exception: Exception) {
                        _state.value = state.copy(stories = NetworkResource.Error(exception))
                    }
                }
                state.copy(stories = NetworkResource.Loading())
            }
        }
    }

    private suspend fun loadStories(): List<Story> = dicodingStoryDataSource.getStories()
}