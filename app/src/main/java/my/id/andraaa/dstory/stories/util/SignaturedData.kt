package my.id.andraaa.dstory.stories.util

import java.security.MessageDigest

class SignaturedData(private var _value: ByteArray) {
    private val md5MessageDigest = MessageDigest.getInstance("MD5")
    var value: ByteArray
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
        _signature = md5MessageDigest.digest(value)
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