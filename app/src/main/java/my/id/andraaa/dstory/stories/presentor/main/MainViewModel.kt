package my.id.andraaa.dstory.stories.presentor.main

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.stories.data.AuthDataSource
import my.id.andraaa.dstory.stories.data.Session
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.util.MVIViewModel

data class MainState(
    val session: NetworkResource<Session?> = NetworkResource.Loading()
)

sealed class MainAction {
    object LoadSession : MainAction()
    object SignOut : MainAction()
}

object MainSideEffect


class MainViewModel(private val authDataSource: AuthDataSource) :
    MVIViewModel<MainState, MainAction, MainSideEffect>(MainState()) {

    init {
        dispatch(MainAction.LoadSession)
    }

    override fun reducer(state: MainState, action: MainAction): MainState {
        return when (action) {
            MainAction.LoadSession -> {
                viewModelScope.launch {
                    _state.value = state.copy(session = NetworkResource.Loaded(loadSession()))
                }
                state.copy(session = NetworkResource.Loading())
            }
            MainAction.SignOut -> {
                viewModelScope.launch {
                    signOut()
                }
                state
            }
        }
    }

    private suspend fun loadSession(): Session? = authDataSource.getSession()

    private fun signOut() = authDataSource.signOut()
}
