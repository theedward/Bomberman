package com.cmov.bomberman.model;

import android.graphics.Canvas;

// Every object that will be printed on the screen must implement this
// interface.
public abstract class Drawing {
	private Position position;

	protected Drawing(final Position position) {
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public abstract void draw(Canvas canvas);
}
