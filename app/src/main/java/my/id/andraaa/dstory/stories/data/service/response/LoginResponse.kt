package my.id.andraaa.dstory.stories.data.service.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "loginResult")
    val loginResult: LoginResult,
    @Json(name = "error")
    override val error: Boolean,
    @Json(name = "message")
    override val message: String,
) : BaseResponse(
    error = error,
    message = message
)