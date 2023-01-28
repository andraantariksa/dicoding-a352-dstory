package my.id.andraaa.dstory.stories.data.service.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResult(
    @Json(name = "name")
    val name: String,
    @Json(name = "token")
    val token: String,
    @Json(name = "userId")
    val userId: String
)