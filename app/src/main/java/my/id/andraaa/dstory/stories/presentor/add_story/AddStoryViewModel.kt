package my.id.andraaa.dstory.stories.presentor.add_story

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.stories.data.DicodingStoryDataSource
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.util.MVIViewModel
import my.id.andraaa.dstory.stories.util.SignaturedData

object AddStorySideEffect

sealed class AddStoryAction {
    class ChangeImage(val value: ByteArray) : AddStoryAction()
    class ChangeDescription(val value: String) : AddStoryAction()
    object ProceedAddStory : AddStoryAction()
    object Reset : AddStoryAction()
    object RemoveImage : AddStoryAction()
}

data class AddStoryState(
    val image: SignaturedData? = null,
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
            is AddStoryAction.ChangeImage -> {
                state.copy(image = SignaturedData(action.value))
            }
            is AddStoryAction.ChangeDescription -> state.copy(description = action.value)
            AddStoryAction.ProceedAddStory -> {
                viewModelScope.launch(Dispatchers.IO) {
                    addStory(state.image, state.description)
                }
                state
            }
            is AddStoryAction.Reset -> AddStoryState()
            AddStoryAction.RemoveImage -> state.copy(image = null)
        }
    }

    private suspend fun addStory(image: SignaturedData?, description: String) {
        _state.value = state.value.copy(addStoryState = NetworkResource.Loading())
        try {
            dicodingStoryDataSource.addStory(image?.value, description, 0.0F, 0.0F)
            _state.value = state.value.copy(addStoryState = NetworkResource.Loaded(Unit))
        } catch (exception: Exception) {
            _state.value = state.value.copy(addStoryState = NetworkResource.Error(exception))
        }
    }
}