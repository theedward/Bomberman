package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Position;

public class ObstacleDrawing extends Drawing {
	private static Bitmap[] sprite;

	public ObstacleDrawing(final Position position, final int step) {
		super(position);
		this.setStep(step);

		if (sprite == null) {
			sprite = GameUtils.getInstance().readObstacleSprite();
		}
	}

	@Override
	public void draw(final Canvas canvas) {
		final int spriteWidth = sprite[0].getWidth();
		final int spriteHeight = sprite[0].getHeight();
		final int x = (int) (getPosition().getX() * spriteWidth);
		final int y = (int) (getPosition().getY() * spriteHeight);

		canvas.drawBitmap(sprite[this.getStep()], x, y, null);
	}
}
