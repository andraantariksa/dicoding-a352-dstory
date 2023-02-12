package my.id.andraaa.dstory

import my.id.andraaa.dstory.stories.data.service.response.Story

class StoryFactory {
    fun createStory(): Story {
        return Story(
            createdAt = "",
            id = "story-FvU4u0Vp2S3PMsFg",
            photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
            name = "Dimas",
            description = "Lorem Ipsum",
            lat = -10.212,
            lon = -16.002
        )
    }
}