package com.jil.paintf.custom

import android.content.res.Resources
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.bottomsheets.GridItem

class BackgroundGridItem(@ColorRes private val color: Int, override val title: String) : GridItem {

    override fun populateIcon(imageView: ImageView) {
        imageView.apply {
            setBackgroundColor(ContextCompat.getColor(imageView.context, color))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                marginEnd = 4.dp
                marginStart = 4.dp
            }
        }
    }

    private val Int.dp: Int get() = toFloat().dp.toInt()

    private val Float.dp: Float
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
        )
}