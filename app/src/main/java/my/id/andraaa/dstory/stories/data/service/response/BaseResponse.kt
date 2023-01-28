package my.id.andraaa.dstory.stories.data.service.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class BaseResponse(
    @Json(name = "error")
    open val error: Boolean,
    @Json(name = "message")
    open val message: String
)