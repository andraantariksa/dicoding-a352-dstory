package my.id.andraaa.dstory.stories.domain

import my.id.andraaa.dstory.stories.data.DicodingStoryDataSourceImpl
import my.id.andraaa.dstory.stories.data.service.response.Story

interface DicodingStoryDataSource {
    suspend fun getStories(page: Int = DicodingStoryDataSourceImpl.DICODING_STORY_STARTING_PAGE): List<Story>
    suspend fun getStoriesQuantity(
        quantity: Int = 100, withLocationOnly: Boolean = false
    ): List<Story>

    suspend fun getStory(id: String): Story
    suspend fun addStory(
        imageBytes: ByteArray, description: String, lat: Float?, lon: Float?
    )
}
