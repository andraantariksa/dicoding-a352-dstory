package my.id.andraaa.dstory.stories.data

import android.content.Context
import android.net.Uri
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import my.id.andraaa.dstory.stories.data.service.DicodingStoryService
import my.id.andraaa.dstory.stories.data.service.response.Story
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.URI
import java.net.URLEncoder

class DicodingStoryDataSource(
    private val context: Context,
    moshi: Moshi,
    private val dicodingStoryService: DicodingStoryService,
    private val authDataSource: AuthDataSource,
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

    suspend fun addStory(imageUri: Uri, description: String, lat: Float, lon: Float) =
        withContext(Dispatchers.IO) {
            val imageJavaUri = URI(URLEncoder.encode(imageUri.toString(), "UTF-8"))
            val file = File(imageJavaUri)

            val filePart = MultipartBody.Part.createFormData(
                "image",
                file.name,
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            )

            val token = authDataSource.getSession()
            dicodingStoryService.addStory(
                "Bearer $token",
                filePart,
                description,
                lat,
                lon
            )
        }
}