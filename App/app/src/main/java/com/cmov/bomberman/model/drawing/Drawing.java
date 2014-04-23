package com.cmov.bomberman.model.drawing;

import android.graphics.Canvas;
import com.cmov.bomberman.model.Position;

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
