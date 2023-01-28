package my.id.andraaa.dstory.stories.presentor.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import my.id.andraaa.dstory.R

class HeaderView : View {
    private val paint: Paint

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        val typedArray = context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.HeaderView,
            0,
            0
        )
        val shapeColor = typedArray.getColor(R.styleable.HeaderView_color, Color.TRANSPARENT)
        typedArray.recycle()

        paint = Paint().apply {
            color = shapeColor
        }
    }

    constructor(context: Context, attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        0
    )

    constructor(context: Context) : this(
        context,
        null
    )

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawCircle(width.toFloat() / 2.0F, 0F, height.toFloat(), paint)
    }
}