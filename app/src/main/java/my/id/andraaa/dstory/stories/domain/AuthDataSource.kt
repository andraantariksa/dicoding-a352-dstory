package my.id.andraaa.dstory.stories.domain

interface AuthDataSource {
    suspend fun getSession(): Session?
    suspend fun signIn(email: String, password: String): Unit
    suspend fun signUp(name: String, email: String, password: String): Unit
    fun signOut()
}