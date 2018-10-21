package com.codingspezis.android.metalonly.player.views

import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.support.v7.widget.AppCompatTextView
import android.text.TextUtils.TruncateAt
import android.util.AttributeSet
import android.view.Gravity

/**
 * class similar to TextView scrolls from right to left if view width is greater
 * than display width
 */
class Marquee(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    init {
        setSingleLine()
        setTypeface(null, Typeface.BOLD)
        ellipsize = TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        gravity = Gravity.CENTER_HORIZONTAL
    }

    override fun onFocusChanged(
        focused: Boolean,
        direction: Int,
        previouslyFocusedRect: Rect?
    ) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect)
        }
    }

    override fun onWindowFocusChanged(focused: Boolean) {
        if (focused) {
            super.onWindowFocusChanged(focused)
        }
    }

    override fun isFocused(): Boolean {
        return true
    }
}
