package my.id.andraaa.dstory.stories.data.service.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StoryResponse(
    @Json(name = "error")
    override val error: Boolean,
    @Json(name = "message")
    override val message: String,
    @Json(name = "story")
    val story: Story
) : BaseResponse(
    error = error,
    message = message
)