package com.cmov.bomberman.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.meic.cmov.bomberman.R;

public class Maze implements Drawable {
	Bitmap backgroundImage;

	public Maze() {
		backgroundImage = GameUtils.readBitmapFromResource(R.drawable.maze);
	}

	// TODO: verify that this image occupies exactly the all canvas.
	@Override
	public void draw(final Canvas canvas) {
		// this image must occupy the all canvas
		canvas.drawBitmap(backgroundImage, 0, 0, null);
	}
}
