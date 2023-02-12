package my.id.andraaa.dstory.stories.presentor.auth.signup

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.stories.data.AuthDataSourceImpl
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.util.MVIViewModel

object SignUpSideEffect

sealed class SignUpAction {
    class ChangeName(val value: String) : SignUpAction()
    class ChangeEmail(val value: String, val error: Boolean) : SignUpAction()
    class ChangePassword(val value: String, val error: Boolean) : SignUpAction()
    object ProceedSignUp : SignUpAction()
}

data class SignUpState(
    val name: String = "",
    val email: String = "",
    val emailIsError: Boolean = true,
    val password: String = "",
    val passwordIsError: Boolean = true,
    val signUpState: NetworkResource<Unit>? = null
) {
    fun formIsValid(): Boolean = !emailIsError && !passwordIsError
}

class SignUpViewModel(
    private val authDataSource: AuthDataSourceImpl
) : MVIViewModel<SignUpState, SignUpAction, SignUpSideEffect>(SignUpState()) {
    override fun reducer(state: SignUpState, action: SignUpAction): SignUpState {
        return when (action) {
            is SignUpAction.ChangeEmail -> state.copy(
                email = action.value,
                emailIsError = action.error
            )
            is SignUpAction.ChangePassword -> state.copy(
                password = action.value,
                passwordIsError = action.error
            )
            is SignUpAction.ChangeName -> state.copy(
                name = action.value
            )
            SignUpAction.ProceedSignUp -> {
                viewModelScope.launch {
                    signUp(state.name, state.email, state.password)
                }
                state.copy(signUpState = NetworkResource.Loading())
            }
        }
    }

    private suspend fun signUp(name: String, email: String, password: String) {
        try {
            authDataSource.signUp(name, email, password)
            _state.value = state.value.copy(signUpState = NetworkResource.Loaded(Unit))
        } catch (exception: Exception) {
            _state.value = state.value.copy(signUpState = NetworkResource.Error(exception))
        }
    }
}