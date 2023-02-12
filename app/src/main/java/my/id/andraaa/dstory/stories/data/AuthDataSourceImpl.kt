package my.id.andraaa.dstory.stories.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import my.id.andraaa.dstory.stories.data.service.DicodingStoryException
import my.id.andraaa.dstory.stories.data.service.DicodingStoryService
import my.id.andraaa.dstory.stories.data.service.response.BaseResponse
import my.id.andraaa.dstory.stories.domain.AuthDataSource
import my.id.andraaa.dstory.stories.domain.Session
import retrofit2.HttpException

class AuthDataSourceImpl(
    context: Context,
    moshi: Moshi,
    private val dicodingStoryService: DicodingStoryService,
) : AuthDataSource {
    private val baseAdapter = moshi.adapter(BaseResponse::class.java).lenient()
    private val sharedPreferences =
        EncryptedSharedPreferences(context, "settings", MasterKey(context))

    @Suppress("RedundantSuspendModifier")
    override suspend fun getSession(): Session? {
        val token = sharedPreferences.getString(AUTH_TOKEN_KEY, null)
        val name = sharedPreferences.getString(NAME_KEY, null)
        return if (name != null && token != null) {
            Session(name, token)
        } else {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    private suspend fun setToken(session: Session) =
        sharedPreferences.edit().apply {
            putString(AUTH_TOKEN_KEY, session.token)
            putString(NAME_KEY, session.name)
        }.apply()

    override suspend fun signIn(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response =
                dicodingStoryService.login(DicodingStoryService.LoginData(email, password))
            val token = response.loginResult.token
            val name = response.loginResult.name
            setToken(Session(name, token))
        } catch (exception: HttpException) {
            exception.response()?.errorBody()?.string()?.let { rawErrorResponse ->
                baseAdapter.fromJson(rawErrorResponse)?.let { errorResponse ->
                    throw DicodingStoryException(errorResponse.message, exception)
                }
            }
            throw exception
        }
    }

    override suspend fun signUp(name: String, email: String, password: String): Unit =
        withContext(Dispatchers.IO) {
            try {
                dicodingStoryService.register(
                    DicodingStoryService.RegisterData(
                        name,
                        email,
                        password
                    )
                )
            } catch (exception: HttpException) {
                exception.response()?.errorBody()?.string()?.let { rawErrorResponse ->
                    baseAdapter.fromJson(rawErrorResponse)?.let { errorResponse ->
                        throw DicodingStoryException(errorResponse.message, exception)
                    }
                }
                throw exception
            }
        }

    override fun signOut() {
        sharedPreferences.edit().remove(AUTH_TOKEN_KEY).apply()
    }

    companion object {
        const val AUTH_TOKEN_KEY = "auth_token"
        const val NAME_KEY = "name"
    }
}