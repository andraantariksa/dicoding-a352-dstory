package my.id.andraaa.dstory.stories.presentor.auth.signup

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.stories.data.AuthDataSource
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.util.MVIViewModel

object SignUpSideEffect

sealed class SignUpAction {
    class ChangeName(val value: String) : SignUpAction()
    class ChangeEmail(val value: String) : SignUpAction()
    class ChangePassword(val value: String) : SignUpAction()
    object ProceedSignUp : SignUpAction()
}

data class SignUpState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val signUpState: NetworkResource<Unit>? = null
) {
    fun formIsValid(): Boolean = email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()
}

class SignUpViewModel(
    private val authDataSource: AuthDataSource
) : MVIViewModel<SignUpState, SignUpAction, SignUpSideEffect>(SignUpState()) {
    override fun reducer(state: SignUpState, action: SignUpAction): SignUpState {
        return when (action) {
            is SignUpAction.ChangeEmail -> state.copy(email = action.value)
            is SignUpAction.ChangePassword -> state.copy(password = action.value)
            is SignUpAction.ChangeName -> state.copy(name = action.value)
            SignUpAction.ProceedSignUp -> {
                viewModelScope.launch {
                    signUp(state.name, state.email, state.password)
                }
                state
            }
        }
    }

    private suspend fun signUp(name: String, email: String, password: String) {
        _state.value = state.value.copy(signUpState = NetworkResource.Loading())
        try {
            authDataSource.signUp(name, email, password)
            _state.value = state.value.copy(signUpState = NetworkResource.Loaded(Unit))
        } catch (exception: Exception) {
            _state.value = state.value.copy(signUpState = NetworkResource.Error(exception))
        }
    }
}