package my.id.andraaa.dstory.stories.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import my.id.andraaa.dstory.stories.data.service.DicodingStoryService
import my.id.andraaa.dstory.stories.data.service.response.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class DicodingStoryServiceMock : DicodingStoryService {
    override suspend fun login(data: DicodingStoryService.LoginData): LoginResponse {
        TODO("Not yet implemented")
    }

    override suspend fun register(data: DicodingStoryService.RegisterData): BaseResponse {
        TODO("Not yet implemented")
    }

    override suspend fun addStory(
        file: MultipartBody.Part?,
        description: String,
        lat: Float,
        lon: Float
    ): BaseResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStories(
        page: Int,
        size: Int?,
        filterWithLocation: Int
    ): StoriesResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStory(id: String): StoryResponse {
        TODO("Not yet implemented")
    }
}

class DicodingStoryDataSource(
    private val dicodingStoryService: DicodingStoryService,
) {
    suspend fun getStories(page: Int = 1): List<Story> {
        val stories = dicodingStoryService.getStories(page)
        return stories.listStory
    }

    suspend fun getStoriesQuantity(
        quantity: Int = 100,
        withLocationOnly: Boolean = false
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