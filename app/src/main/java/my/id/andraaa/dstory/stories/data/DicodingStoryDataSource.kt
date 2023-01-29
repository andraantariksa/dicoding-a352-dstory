package my.id.andraaa.dstory.stories.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import my.id.andraaa.dstory.stories.data.service.DicodingStoryService
import my.id.andraaa.dstory.stories.data.service.response.Story
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class DicodingStoryDataSource(
    private val dicodingStoryService: DicodingStoryService,
    private val authDataSource: AuthDataSource,
    private val context: Context
) {
    suspend fun getStories(): List<Story> {
        val token = authDataSource.getSession()?.token
        val stories = dicodingStoryService.getStories("Bearer $token")
        return stories.listStory
    }

    suspend fun getStory(id: String): Story {
        val token = authDataSource.getSession()?.token
        val story = dicodingStoryService.getStory("Bearer $token", id)
        return story.story
    }

    suspend fun addStory(imageUri: Uri?, description: String, lat: Float, lon: Float) =
        withContext(Dispatchers.IO) {
            val filePart = if (imageUri != null) {
                val stream = context.contentResolver.openInputStream(imageUri)!!

                val bytes = stream.readBytes()
                val body =
                    bytes.toRequestBody("multipart/form-data".toMediaTypeOrNull(), 0, bytes.size)

                stream.close()

                MultipartBody.Part.createFormData(
                    "photo",
                    "image.png",
                    body
                )
            } else {
                null
            }

            val session = authDataSource.getSession()
            dicodingStoryService.addStory(
                "Bearer ${session!!.token}",
                filePart,
                description,
                lat,
                lon
            )
        }
}