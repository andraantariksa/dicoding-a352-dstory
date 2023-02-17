package my.id.andraaa.dstory.stories.data

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import my.id.andraaa.dstory.stories.data.service.DicodingStoryService
import my.id.andraaa.dstory.stories.data.service.response.Story
import my.id.andraaa.dstory.stories.domain.DicodingStoryDataSource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class DicodingStoryDataSourceImpl(
    private val dicodingStoryService: DicodingStoryService,
) : DicodingStoryDataSource {
    override suspend fun getStories(page: Int): List<Story> {
        val stories = dicodingStoryService.getStories(page)
        return stories.listStory
    }

    override suspend fun getStoriesQuantity(
        quantity: Int, withLocationOnly: Boolean
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

    override suspend fun getStory(id: String): Story {
        val story = dicodingStoryService.getStory(id)
        return story.story
    }

    override suspend fun addStory(
        bitmap: Bitmap, description: String, lat: Float?, lon: Float?
    ): Unit = withContext(Dispatchers.IO) {
        val bytesStream = ByteArrayOutputStream(bitmap.allocationByteCount)
        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 0, bytesStream)

        val bytes = bytesStream.toByteArray()
        val body = bytes.toRequestBody(
            "multipart/form-data".toMediaTypeOrNull(), 0, bytes.size
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