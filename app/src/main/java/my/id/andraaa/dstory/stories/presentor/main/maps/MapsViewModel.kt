package my.id.andraaa.dstory.stories.presentor.main.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.domain.Coordinate
import my.id.andraaa.dstory.stories.domain.DicodingStoryDataSource
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.util.MVIViewModel

data class MapsState(
    val userLocation: NetworkResource<Coordinate>?,
    val stories: NetworkResource<List<Story>>
)

sealed class MapsSideEffect {
    class Snackbar(
        val messsage: String,
        val duration: Int = com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
    ) : MapsSideEffect()
}

sealed class MapsAction {
    class FetchCurrentLocation(val context: Context) : MapsAction()
    object FetchStories : MapsAction()
}

class MapsViewModel(
    private val dicodingStoryDataSource: DicodingStoryDataSource
) :
    MVIViewModel<MapsState, MapsAction, MapsSideEffect>(
        MapsState(
            userLocation = null,
            stories = NetworkResource.Loading(),
        )
    ) {
    init {
        dispatch(MapsAction.FetchStories)
    }

    override fun reducer(state: MapsState, action: MapsAction): MapsState {
        return when (action) {
            is MapsAction.FetchCurrentLocation -> {
                viewModelScope.launch {
                    fetchLocation(action.context)
                }
                state
            }
            MapsAction.FetchStories -> {
                viewModelScope.launch {
                    fetchStories()
                }
                state
            }
        }
    }

    private fun fetchStories() = viewModelScope.launch {
        _state.value = state.value.copy(stories = NetworkResource.Loading())
        try {
            val stories = dicodingStoryDataSource.getStoriesQuantity(withLocationOnly = true)
            _state.value = state.value.copy(stories = NetworkResource.Loaded(stories))
        } catch (exception: Exception) {
            _state.value = state.value.copy(stories = NetworkResource.Error(exception))
            _sideEffect.emit(MapsSideEffect.Snackbar("Error on fetching stories. ${exception.message}"))
        }
    }

    private fun fetchLocation(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val fusedLocation =
            LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            _state.value = state.value.copy(userLocation = NetworkResource.Loading())
            try {
                val location = Tasks.await(fusedLocation.lastLocation)
                _state.value = state.value.copy(
                    userLocation = NetworkResource.Loaded(
                        Coordinate(
                            location.latitude,
                            location.longitude
                        )
                    )
                )
            } catch (exception: Exception) {
                _state.value = state.value.copy(userLocation = NetworkResource.Error(exception))
                _sideEffect.emit(MapsSideEffect.Snackbar("Error on fetching current location. ${exception.message}"))
            }
        } else {
            _state.value = state.value.copy(userLocation = null)
            _sideEffect.emit(MapsSideEffect.Snackbar("We cannot determine your location. Please enable the location permission"))
        }
    }
}