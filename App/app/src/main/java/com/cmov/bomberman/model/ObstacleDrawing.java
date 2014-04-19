package com.cmov.bomberman.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ObstacleDrawing extends Drawing {
	private static final int SPRITE_LINE = 13;
	private static final int SPRITE_COLUMN = 1;
	private static final int NUM_IMAGES = 7;

	private static Bitmap[] sprite;

	private int step;

	public ObstacleDrawing(final Position position, final int step) {
		super(position);
		this.step = step;

		// load sprites if it's the first obstacle
		if (sprite == null) {
			sprite = GameUtils.readCharacterSprite(SPRITE_LINE, SPRITE_COLUMN, NUM_IMAGES);
		}
	}

	@Override
	public void draw(final Canvas canvas) {
		canvas.drawBitmap(sprite[step], getPosition().getX(), getPosition().getY(), null);
	}
}
