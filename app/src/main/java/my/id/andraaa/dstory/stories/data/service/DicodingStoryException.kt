package my.id.andraaa.dstory.stories.data.service

class DicodingStoryException(
    override val message: String? = null,
    override val cause: Throwable? = null
) :
    Exception(message, cause)
