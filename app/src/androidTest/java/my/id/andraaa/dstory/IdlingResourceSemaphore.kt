package my.id.andraaa.dstory

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

class IdlingResourceSemaphore(
    private val resourceName: String
) {
    private val countingIdlingResource = CountingIdlingResource(resourceName)

    val idlingResource: IdlingResource
        get() = countingIdlingResource

    fun increment(count: Int = 1) {
        repeat(count) {
            countingIdlingResource.increment()
        }
    }

    fun decrement(count: Int = 1) {
        repeat(count) {
            countingIdlingResource.decrement()
        }
    }
}