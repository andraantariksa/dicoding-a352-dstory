package my.id.andraaa.dstory.stories.util

import android.util.Patterns

fun String.isEmailValid() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isPasswordValid() = this.length >= 8
