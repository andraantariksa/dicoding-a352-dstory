package my.id.andraaa.dstory.stories.presentor.add_story

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.stories.domain.DicodingStoryDataSource
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.util.MVIViewModel
import my.id.andraaa.dstory.stories.util.SignaturedData

object AddStorySideEffect

sealed class AddStoryAction {
    class ChangeImage(val value: ByteArray) : AddStoryAction()
    class ChangeDescription(val value: String) : AddStoryAction()
    class ProceedAddStory(val context: Context) : AddStoryAction()
    object Reset : AddStoryAction()
    object RemoveImage : AddStoryAction()
    object ToggleShareCurrentLocation : AddStoryAction()
}

data class AddStoryState(
    val image: SignaturedData? = null,
    val description: String = "",
    val addStoryState: NetworkResource<Unit>? = null,
    val shareCurrentLocation: Boolean = false,
) {
    fun formIsValid(): Boolean = description.isNotEmpty() && image != null
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
            is AddStoryAction.ProceedAddStory -> {
                viewModelScope.launch(Dispatchers.IO) {
                    addStory(action.context, state.image, state.description)
                }
                state
            }
            is AddStoryAction.Reset -> AddStoryState()
            AddStoryAction.RemoveImage -> state.copy(image = null)
            AddStoryAction.ToggleShareCurrentLocation -> state.copy(shareCurrentLocation = !state.shareCurrentLocation)
        }
    }

    private suspend fun addStory(context: Context?, image: SignaturedData?, description: String) {
        _state.value = state.value.copy(addStoryState = NetworkResource.Loading())
        val location = if (context != null && (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            var exception = Exception("Unknown error")
            val result = try {
                val fusedLocation = LocationServices.getFusedLocationProviderClient(context)
                Tasks.await(fusedLocation.lastLocation)
            } catch (_exception: Exception) {
                exception = _exception
                null
            }

            if (result == null) {
                _state.value = state.value.copy(addStoryState = NetworkResource.Error(exception))
                return
            }

            result
        } else {
            null
        }

        try {
            dicodingStoryDataSource.addStory(
                image?.value ?: ByteArray(0),
                description,
                location!!.latitude.toFloat(),
                location.longitude.toFloat()
            )
            _state.value = state.value.copy(addStoryState = NetworkResource.Loaded(Unit))
        } catch (exception: Exception) {
            _state.value = state.value.copy(addStoryState = NetworkResource.Error(exception))
        }
    }
}