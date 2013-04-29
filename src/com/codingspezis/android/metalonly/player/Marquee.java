package com.codingspezis.android.metalonly.player;

import android.content.*;
import android.graphics.*;
import android.text.TextUtils.TruncateAt;
import android.util.*;
import android.widget.*;

/**
 * Marquee
 * 
 * @version 22.12.2012
 * 
 *          class similar to TextView scrolls from right to left if view width
 *          is greater than display width
 * 
 */
public class Marquee extends TextView {

	public Marquee(Context context, AttributeSet attrs) {
		super(context, attrs);
		// text format
		setSingleLine();
		setEllipsize(TruncateAt.MARQUEE);
		setMarqueeRepeatLimit(-1);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		if (focused) {
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean focused) {
		if (focused) {
			super.onWindowFocusChanged(focused);
		}
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}
