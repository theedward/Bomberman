package com.cmov.bomberman.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Maze implements Drawable {
	// The bitmaps properties
	private static final int IMAGE_X = 0;
	private static final int IMAGE_Y = 0;
	private static final int IMAGE_WIDTH = 240;
	private static final int IMAGE_HEIGHT = 160;

	private Bitmap maze;

	public Maze() {
		Bitmap bm = GameUtils.readLevelBitmap(IMAGE_X, IMAGE_Y, IMAGE_WIDTH, IMAGE_HEIGHT);
	}

	@Override
	public void draw(final Canvas canvas) {
		// the bitmap must be the same size of the canvas
		if (maze.getWidth() != canvas.getWidth() || maze.getHeight() != canvas.getHeight()) {
			maze = Bitmap.createScaledBitmap(maze, canvas.getWidth(), canvas.getHeight(), true);
		}
		canvas.drawBitmap(maze, 0, 0, null);
	}
}