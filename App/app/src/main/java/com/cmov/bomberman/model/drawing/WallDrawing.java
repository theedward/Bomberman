package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Position;

public class WallDrawing extends Drawing {
	private static Bitmap sprite;

	public WallDrawing(final Position position) {
		super(position);
		if (sprite == null) {
			sprite = GameUtils.readWallSprite();
		}
	}

	@Override
	public void draw(final Canvas canvas) {
		canvas.drawBitmap(sprite, (int) getPosition().getX(), (int) getPosition().getY(), null);
	}
}
