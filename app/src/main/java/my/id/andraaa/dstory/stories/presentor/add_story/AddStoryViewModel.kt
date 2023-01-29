package my.id.andraaa.dstory.stories.presentor.add_story

import android.net.Uri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.stories.data.DicodingStoryDataSource
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.util.MVIViewModel

object AddStorySideEffect

sealed class AddStoryAction {
    class ChangeImage(val value: Uri) : AddStoryAction()
    class ChangeDescription(val value: String) : AddStoryAction()
    object ProceedAddStory : AddStoryAction()
    object Reset : AddStoryAction()
    object RemoveImage : AddStoryAction()
}

data class AddStoryState(
    val image: Uri? = null,
    val description: String = "",
    val addStoryState: NetworkResource<Unit>? = null
) {
    fun formIsValid(): Boolean = description.isNotEmpty()
}

class AddStoryViewModel(
    private val dicodingStoryDataSource: DicodingStoryDataSource,
) : MVIViewModel<AddStoryState, AddStoryAction, AddStorySideEffect>(AddStoryState()) {
    override fun reducer(state: AddStoryState, action: AddStoryAction): AddStoryState {
        return when (action) {
            is AddStoryAction.ChangeImage -> state.copy(image = action.value)
            is AddStoryAction.ChangeDescription -> state.copy(description = action.value)
            AddStoryAction.ProceedAddStory -> {
                viewModelScope.launch {
                    addStory(state.image, state.description)
                }
                state.copy(addStoryState = NetworkResource.Loading())
            }
            is AddStoryAction.Reset -> AddStoryState()
            AddStoryAction.RemoveImage -> state.copy(image = null)
        }
    }

    private suspend fun addStory(image: Uri?, description: String) {
        try {
            dicodingStoryDataSource.addStory(image, description, 0.0F, 0.0F)
            _state.value = state.value.copy(addStoryState = NetworkResource.Loaded(Unit))
        } catch (exception: Exception) {
            _state.value = state.value.copy(addStoryState = NetworkResource.Error(exception))
        }
    }
}