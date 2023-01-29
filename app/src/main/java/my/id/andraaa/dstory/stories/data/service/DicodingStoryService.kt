package my.id.andraaa.dstory.stories.data.service

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import my.id.andraaa.dstory.stories.data.service.response.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface DicodingStoryService {

    @JsonClass(generateAdapter = true)
    data class LoginData(
        @Json(name = "email")
        val email: String,
        @Json(name = "password")
        val password: String,
    )

    @POST("login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body data: LoginData): LoginResponse

    @JsonClass(generateAdapter = true)
    data class RegisterData(
        @Json(name = "name")
        val name: String,
        @Json(name = "email")
        val email: String,
        @Json(name = "password")
        val password: String,
    )

    @POST("register")
    @Headers("Content-Type: application/json")
    suspend fun register(
        @Body data: RegisterData
    ): BaseResponse

    @Multipart
    @Headers("Content-Type: multipart/form-data")
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: String,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float,
    ): BaseResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") filterWithLocation: Int = 0,
    ): StoriesResponse

    @GET("stories/{id}")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): StoryResponse

    companion object {
        const val BASE_URL = "https://story-api.dicoding.dev/v1/"
    }
}