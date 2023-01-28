package my.id.andraaa.dstory.stories.util

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class MVIViewModel<STATE, ACTION, SIDE_EFFECT>(initialState: STATE) : ViewModel() {
    protected val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    protected val _sideEffect = MutableSharedFlow<SIDE_EFFECT>()
    val sideEffect = _sideEffect.asSharedFlow()

    protected abstract fun reducer(state: STATE, action: ACTION): STATE

    open fun dispatch(action: ACTION) {
        _state.value = reducer(state.value, action)
    }
}