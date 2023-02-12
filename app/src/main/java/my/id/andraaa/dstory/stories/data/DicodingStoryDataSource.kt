package my.id.andraaa.dstory.stories.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import my.id.andraaa.dstory.stories.data.service.DicodingStoryService
import my.id.andraaa.dstory.stories.data.service.response.Story
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class DicodingStoryDataSource(
    private val dicodingStoryService: DicodingStoryService,
) {
    suspend fun getStories(page: Int = DICODING_STORY_STARTING_PAGE): List<Story> {
        val stories = dicodingStoryService.getStories(page)
        return stories.listStory
    }

    suspend fun getStoriesQuantity(
        quantity: Int = 100, withLocationOnly: Boolean = false
    ): List<Story> {
        val stories = mutableListOf<Story>()
        var currentPage = 1
        while (true) {
            val storiesChunk = dicodingStoryService.getStories(
                currentPage, filterWithLocation = if (withLocationOnly) {
                    1
                } else {
                    0
                }
            )
            if (storiesChunk.listStory.isEmpty()) {
                break
            }
            stories.addAll(storiesChunk.listStory)
            if (stories.size > quantity) {
                break
            }
            currentPage += 1
        }
        return stories.take(quantity)
    }

    suspend fun getStory(id: String): Story {
        val story = dicodingStoryService.getStory(id)
        return story.story
    }

    suspend fun addStory(
        imageBytes: ByteArray, description: String, lat: Float, lon: Float
    ): Unit = withContext(Dispatchers.IO) {
        val body = imageBytes.toRequestBody(
            "multipart/form-data".toMediaTypeOrNull(), 0, imageBytes.size
        )

        val filePart = MultipartBody.Part.createFormData(
            "photo", "image.png", body
        )

        dicodingStoryService.addStory(
            filePart, description, lat, lon
        )
    }

    companion object {
        const val DICODING_STORY_STARTING_PAGE = 1
    }
}