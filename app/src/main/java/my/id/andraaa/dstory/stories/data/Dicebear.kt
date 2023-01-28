package my.id.andraaa.dstory.stories.data

import android.net.Uri

object Dicebear {
    fun getAvatarUrl(seed: String): String {
        return Uri.parse(BASE_URL)
            .buildUpon()
            .appendPath("initials")
            .appendPath("png")
            .appendQueryParameter("seed", seed)
            .build()
            .toString()
    }

    private const val BASE_URL = "https://api.dicebear.com/5.x/"
}