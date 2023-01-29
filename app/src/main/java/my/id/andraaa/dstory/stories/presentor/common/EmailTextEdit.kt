package my.id.andraaa.dstory.stories.presentor.common

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import my.id.andraaa.dstory.R
import my.id.andraaa.dstory.stories.util.isEmailValid


class EmailTextEdit : TextInputEditText {
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    constructor(context: Context, attributeSet: AttributeSet?) : super(
        context,
        attributeSet
    )

    constructor(context: Context) : super(
        context
    )

    init {
        doOnTextChanged { text, _, _, _ ->
            error = if (text.toString().isEmailValid()) {
                null
            } else {
                context.getString(R.string.email_invalid)
            }
        }
    }
}