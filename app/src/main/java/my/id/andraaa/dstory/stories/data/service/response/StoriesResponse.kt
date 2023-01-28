package my.id.andraaa.dstory.stories.data.service.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StoriesResponse(
    @Json(name = "error")
    override val error: Boolean,
    @Json(name = "listStory")
    val listStory: List<Story>,
    @Json(name = "message")
    override val message: String
) : BaseResponse(
    error = error,
    message = message
)