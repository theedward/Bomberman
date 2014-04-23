package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.Position;

public class ObstacleDrawing extends Drawing {
	private static final int SPRITE_LINE = 13;
	private static final int SPRITE_COLUMN = 1;
	private static final int NUM_IMAGES = 7;

	private static Bitmap[] sprite;

	private int step;

	public ObstacleDrawing(final Position position, final int step) {
		super(position);
		this.step = step;
	}

	@Override
	public void draw(final Canvas canvas) {
	}
}
