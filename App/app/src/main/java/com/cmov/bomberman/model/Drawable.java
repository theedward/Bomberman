package com.cmov.bomberman.model;

import android.graphics.Canvas;

// Every object that will be printed on the screen must implement this
// interface.
public interface Drawable {
	public void draw(Canvas canvas);
}
