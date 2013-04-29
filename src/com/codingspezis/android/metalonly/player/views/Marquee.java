package com.codingspezis.android.metalonly.player.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Marquee
 * @version 22.12.2012
 *
 * class similar to TextView
 * scrolls from right to left if view width is greater than display width
 *
 */
public class Marquee extends TextView{
	
	
	public Marquee(Context context, AttributeSet attrs) {
		super(context, attrs);
		// text format
		setSingleLine();
		setTypeface(null, Typeface.BOLD);
		setEllipsize(TruncateAt.MARQUEE);
		setMarqueeRepeatLimit(-1);
		setGravity(Gravity.CENTER_HORIZONTAL);
	}
	
	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
	    if(focused)
	        super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	@Override
	public void onWindowFocusChanged(boolean focused) {
	    if(focused)
	        super.onWindowFocusChanged(focused);
	}

	@Override
	public boolean isFocused() {
	    return true;
	}
}
