package my.id.andraaa.dstory.stories.util

import android.graphics.Bitmap
import java.nio.ByteBuffer
import java.security.MessageDigest

class SignaturedData(private var _value: Bitmap) {
    private val md5MessageDigest = MessageDigest.getInstance("MD5")
    var value: Bitmap
        get() = _value
        set(newValue) {
            _value = newValue

            refreshSignature()
        }

    private lateinit var _signature: ByteArray
    val signature
        get() = _signature

    init {
        refreshSignature()
    }

    fun refreshSignature() {
        val buffer = ByteBuffer.allocate(value.allocationByteCount)
        value.copyPixelsToBuffer(buffer)
        _signature = md5MessageDigest.digest(buffer.array())
    }

    override fun equals(other: Any?): Boolean {
        if (other is SignaturedData) {
            return _signature.contentEquals(other._signature)
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return _signature.hashCode()
    }
}