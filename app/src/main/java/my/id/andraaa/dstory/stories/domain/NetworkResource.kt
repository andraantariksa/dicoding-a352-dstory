package my.id.andraaa.dstory.stories.domain

sealed class NetworkResource<T> {
    class Error<T>(val error: Exception) : NetworkResource<T>()
    class Loading<T> : NetworkResource<T>()
    class Loaded<T>(val data: T) : NetworkResource<T>()

    val dataOrNull: T?
        get() = if (this is Loaded) {
            this.data
        } else {
            null
        }
}
