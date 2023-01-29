package my.id.andraaa.dstory.stories.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun File.getContentUri(context: Context): Uri = FileProvider.getUriForFile(context, AUTHORITY, this)

const val AUTHORITY = "my.id.andraaa.dstory.provider"
