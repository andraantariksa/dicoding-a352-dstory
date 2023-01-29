package my.id.andraaa.dstory.stories.presentor.story

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.stories.data.DicodingStoryDataSource
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.util.MVIViewModel

data class StoryState(
    val story: NetworkResource<Story?> = NetworkResource.Loading()
)

sealed class StoryAction {
    class LoadStory(val id: String) : StoryAction()
}

object StorySideEffect


class StoryViewModel(
    private val dicodingStoryDataSource: DicodingStoryDataSource,
) :
    MVIViewModel<StoryState, StoryAction, StorySideEffect>(StoryState()) {

    override fun reducer(state: StoryState, action: StoryAction): StoryState {
        return when (action) {
            is StoryAction.LoadStory -> {
                viewModelScope.launch {
                    try {
                        _state.value =
                            state.copy(story = NetworkResource.Loaded(loadStory(action.id)))
                    } catch (exception: Exception) {
                        _state.value =
                            state.copy(story = NetworkResource.Error(exception))
                    }
                }
                state.copy(story = NetworkResource.Loading())
            }
        }
    }

    private suspend fun loadStory(id: String): Story = dicodingStoryDataSource.getStory(id)
}
