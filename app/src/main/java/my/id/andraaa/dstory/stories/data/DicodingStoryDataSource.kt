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
    suspend fun getStories(page: Int = 1): List<Story> {
        val stories = dicodingStoryService.getStories(page)
        return stories.listStory
    }

    suspend fun getStory(id: String): Story {
        val story = dicodingStoryService.getStory(id)
        return story.story
    }

    suspend fun addStory(imageBytes: ByteArray?, description: String, lat: Float, lon: Float) =
        withContext(Dispatchers.IO) {
            val filePart = if (imageBytes != null) {
                val body = imageBytes.toRequestBody(
                    "multipart/form-data".toMediaTypeOrNull(),
                    0,
                    imageBytes.size
                )

                MultipartBody.Part.createFormData(
                    "photo",
                    "image.png",
                    body
                )
            } else {
                null
            }

            dicodingStoryService.addStory(
                filePart,
                description,
                lat,
                lon
            )
        }
}