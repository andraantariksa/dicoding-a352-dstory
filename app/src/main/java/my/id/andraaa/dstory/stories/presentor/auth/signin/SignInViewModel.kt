package my.id.andraaa.dstory.stories.presentor.auth.signin

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.stories.domain.AuthDataSource
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.util.MVIViewModel

object SignInSideEffect

sealed class SignInAction {
    class ChangeEmail(val value: String, val isError: Boolean) : SignInAction()
    class ChangePassword(val value: String, val isError: Boolean) : SignInAction()
    object ProceedAddStory : SignInAction()
}

data class SignInState(
    val email: String = "",
    val emailIsError: Boolean = true,
    val password: String = "",
    val passwordIsError: Boolean = true,
    val signInState: NetworkResource<Unit>? = null
) {
    fun formIsValid(): Boolean = !emailIsError && !passwordIsError
}

class SignInViewModel(
    private val authDataSource: AuthDataSource,
) : MVIViewModel<SignInState, SignInAction, SignInSideEffect>(SignInState()) {
    override fun reducer(state: SignInState, action: SignInAction): SignInState {
        return when (action) {
            is SignInAction.ChangeEmail -> state.copy(
                email = action.value,
                emailIsError = action.isError
            )
            is SignInAction.ChangePassword -> state.copy(
                password = action.value,
                passwordIsError = action.isError
            )
            SignInAction.ProceedAddStory -> {
                viewModelScope.launch {
                    signIn(state.email, state.password)
                }
                state.copy(signInState = NetworkResource.Loading())
            }
        }
    }

    private suspend fun signIn(email: String, password: String) {
        try {
            authDataSource.signIn(email, password)
            _state.value = state.value.copy(signInState = NetworkResource.Loaded(Unit))
        } catch (exception: Exception) {
            _state.value = state.value.copy(signInState = NetworkResource.Error(exception))
        }
    }
}