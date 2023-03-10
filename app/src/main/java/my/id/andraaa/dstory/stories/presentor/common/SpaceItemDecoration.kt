package my.id.andraaa.dstory.stories.presentor.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class SpaceItemDecoration(private val verticalGap: Int, private val horizontalGap: Int) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = verticalGap / 2
        outRect.top = verticalGap / 2
        outRect.right = horizontalGap / 2
        outRect.left = horizontalGap / 2
    }
}