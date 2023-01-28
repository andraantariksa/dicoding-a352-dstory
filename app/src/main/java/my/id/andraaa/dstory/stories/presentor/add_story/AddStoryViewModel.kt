package my.id.andraaa.dstory.stories.presentor.add_story

import android.net.Uri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.stories.data.AuthDataSource
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.util.MVIViewModel

object AddStorySideEffect

sealed class AddStoryAction {
    class ChangeImage(val value: Uri) : AddStoryAction()
    class ChangeDescription(val value: String) : AddStoryAction()
    object ProceedAddStory : AddStoryAction()
}

data class AddStoryState(
    val image: Uri? = null,
    val description: String = "",
    val addStoryState: NetworkResource<Unit>? = null
) {
    fun formIsValid(): Boolean = description.isNotEmpty()
}

class AddStoryViewModel(
    private val authDataSource: AuthDataSource,
) : MVIViewModel<AddStoryState, AddStoryAction, AddStorySideEffect>(AddStoryState()) {
    override fun reducer(state: AddStoryState, action: AddStoryAction): AddStoryState {
        return when (action) {
            is AddStoryAction.ChangeImage -> state.copy(image = action.value)
            is AddStoryAction.ChangeDescription -> state.copy(description = action.value)
            AddStoryAction.ProceedAddStory -> {
                viewModelScope.launch {
                    addStory(state.image, state.description)
                }
                state
            }
        }
    }

    private suspend fun addStory(image: Uri?, password: String) {
        _state.value = state.value.copy(addStoryState = NetworkResource.Loading())
        try {
//            authDataSource.signIn(email, password)
            _state.value = state.value.copy(addStoryState = NetworkResource.Loaded(Unit))
        } catch (exception: Exception) {
            _state.value = state.value.copy(addStoryState = NetworkResource.Error(exception))
        }
    }
}