package com.experiment.android.picsumgallery.utils.customcomponents

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CustomItemDecorator(private var topBottom: Int = 0, private var leftRight: Int = 0) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = topBottom
        outRect.bottom = topBottom
        outRect.left = leftRight
        outRect.right = leftRight
    }
}